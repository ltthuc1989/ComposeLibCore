package com.ltthuc.billing.impl

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.ltthuc.billing.api.BillingApi
import com.ltthuc.billing.api.model.BillingStatus
import com.ltthuc.billing.api.model.ProductInfo
import com.ltthuc.billing.api.model.PurchaseInfo
import com.ltthuc.utils.secrets.ISecretBillingKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Collections
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

private const val TAG = "BillingManager"
private const val AWAIT_READY_TIMEOUT_MS = 10_000L
private const val RECONNECT_MAX_ATTEMPTS = 6
private const val RECONNECT_BASE_DELAY_MS = 1_000L
private const val RECONNECT_MAX_DELAY_MS = 30_000L

@Singleton
internal class BillingManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val secrets: ISecretBillingKey,
) : BillingApi {

    private val _status = MutableStateFlow<BillingStatus>(BillingStatus.Disconnected)
    override val status: StateFlow<BillingStatus> = _status.asStateFlow()

    private val _purchases = MutableStateFlow<List<PurchaseInfo>>(emptyList())
    override val purchases: StateFlow<List<PurchaseInfo>> = _purchases.asStateFlow()

    // Coalesces parallel connect() calls into one startConnection invocation.
    private val connecting = AtomicBoolean(false)

    // Background scope for reconnect backoff + auto-acknowledge.
    // Cancelled in release(); SupervisorJob so one failed child doesn't kill siblings.
    private val internalScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Purchase tokens currently being acknowledged — prevents double-ack races.
    private val acknowledging: MutableSet<String> = Collections.synchronizedSet(mutableSetOf())

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, list ->
        Log.i(
            TAG,
            "purchasesUpdated responseCode=${result.responseCode} debug=${result.debugMessage} count=${list?.size ?: 0}",
        )
        if (list != null) {
            _purchases.update { list.flatMap(Purchase::toInfo) }
            autoAcknowledgePurchases(list)
        }
    }

    private val client: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            // Note: .enableAutoAcknowledge() requires Play Billing Library v8+.
            // We're on v7.1.1 so we acknowledge manually in autoAcknowledgePurchases().
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build(),
        )
        .build()

    override fun connect() {
        if (client.isReady) {
            Log.i(TAG, "connect() already ready")
            _status.value = BillingStatus.Ready
            return
        }
        // Coalesce: if another startConnection is in flight, skip.
        if (!connecting.compareAndSet(false, true)) {
            Log.i(TAG, "connect() already connecting — skip")
            return
        }
        Log.i(TAG, "connect() startConnection")
        _status.value = BillingStatus.Connecting
        client.startConnection(stateListener(reconnectAttempt = 0))
    }

    private fun stateListener(reconnectAttempt: Int): BillingClientStateListener =
        object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                connecting.set(false)
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "onBillingSetupFinished READY (attempt=$reconnectAttempt)")
                    _status.value = BillingStatus.Ready
                } else {
                    Log.e(
                        TAG,
                        "onBillingSetupFinished ERROR code=${result.responseCode} debug=${result.debugMessage}",
                    )
                    _status.value = BillingStatus.Error(result.responseCode, result.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                connecting.set(false)
                Log.w(TAG, "onBillingServiceDisconnected (lastAttempt=$reconnectAttempt)")
                _status.value = BillingStatus.Disconnected
                scheduleReconnect(reconnectAttempt + 1)
            }
        }

    private fun scheduleReconnect(attempt: Int) {
        if (attempt > RECONNECT_MAX_ATTEMPTS) {
            Log.e(TAG, "reconnect giving up after $RECONNECT_MAX_ATTEMPTS attempts")
            _status.value = BillingStatus.Error(
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                "max reconnect attempts exceeded",
            )
            return
        }
        // Exponential backoff capped at RECONNECT_MAX_DELAY_MS: 1s, 2s, 4s, 8s, 16s, 30s.
        val delayMs = (RECONNECT_BASE_DELAY_MS shl (attempt - 1)).coerceAtMost(RECONNECT_MAX_DELAY_MS)
        Log.i(TAG, "scheduleReconnect attempt=$attempt delayMs=$delayMs")
        internalScope.launch {
            delay(delayMs)
            if (client.isReady) return@launch
            if (!connecting.compareAndSet(false, true)) return@launch
            _status.value = BillingStatus.Connecting
            client.startConnection(stateListener(reconnectAttempt = attempt))
        }
    }

    override fun release() {
        Log.i(TAG, "release()")
        internalScope.cancel()
        if (client.isReady) client.endConnection()
        _status.value = BillingStatus.Disconnected
    }

    /**
     * Suspends until the BillingClient is [BillingStatus.Ready], a terminal
     * [BillingStatus.Error] arrives, or [AWAIT_READY_TIMEOUT_MS] elapses.
     *
     * Returns true if Ready, false otherwise. If the client is currently
     * Disconnected, kicks off [connect] first.
     */
    private suspend fun awaitReady(): Boolean {
        when (val s = _status.value) {
            is BillingStatus.Ready -> return true
            is BillingStatus.Error -> {
                Log.w(TAG, "awaitReady terminal Error code=${s.code} — calling connect()")
                connect()
            }
            is BillingStatus.Disconnected -> connect()
            is BillingStatus.Connecting -> Unit
        }
        val final = withTimeoutOrNull(AWAIT_READY_TIMEOUT_MS) {
            _status.first { it is BillingStatus.Ready || it is BillingStatus.Error }
        }
        return final is BillingStatus.Ready
    }

    /**
     * For each new purchase that is PURCHASED and not yet acknowledged, fire
     * an acknowledge() call in the background. Tracks in-flight tokens to
     * avoid double-ack on rapid listener fires.
     *
     * Without this, Google auto-refunds non-consumable purchases after 3 days.
     */
    private fun autoAcknowledgePurchases(list: List<Purchase>) {
        list.forEach { purchase ->
            if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return@forEach
            if (purchase.isAcknowledged) return@forEach
            val token = purchase.purchaseToken
            if (!acknowledging.add(token)) {
                Log.i(TAG, "autoAck skip (already in-flight) token=${token.take(8)}…")
                return@forEach
            }
            internalScope.launch {
                try {
                    Log.i(TAG, "autoAck start token=${token.take(8)}… products=${purchase.products}")
                    acknowledge(token)
                } finally {
                    acknowledging.remove(token)
                }
            }
        }
    }

    override suspend fun queryProducts(productIds: List<String>): Result<List<ProductInfo>> =
        runCatching {
            val ids = productIds.ifEmpty { secrets.productIds }
            Log.i(TAG, "queryProducts ids=$ids")
            if (ids.isEmpty()) {
                Log.w(TAG, "queryProducts called with empty ids — nothing to query")
                return@runCatching emptyList()
            }
            if (!awaitReady()) error("queryProducts blocked: status=${_status.value}")
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ids.map { id ->
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(id)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    },
                )
                .build()
            val result = client.queryProductDetails(params)
            val br = result.billingResult
            if (br.responseCode != BillingClient.BillingResponseCode.OK) {
                error("queryProductDetails failed code=${br.responseCode} debug=${br.debugMessage}")
            }
            val products = result.productDetailsList.orEmpty().map { it.toInfo() }
            Log.i(TAG, "queryProducts ok count=${products.size} products=${products.map { it.productId }}")
            products
        }.onFailure { Log.e(TAG, "queryProducts failed", it) }

    override suspend fun queryPurchases(): Result<List<PurchaseInfo>> = runCatching {
        Log.i(TAG, "queryPurchases() start clientReady=${client.isReady}")
        if (!awaitReady()) error("queryPurchases blocked: status=${_status.value}")
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val result = client.queryPurchasesAsync(params)
        val br = result.billingResult
        if (br.responseCode != BillingClient.BillingResponseCode.OK) {
            error("queryPurchases failed code=${br.responseCode} debug=${br.debugMessage}")
        }
        val rawList = result.purchasesList
        val mapped = rawList.flatMap(Purchase::toInfo)
        Log.i(TAG, "queryPurchases ok count=${mapped.size} ids=${mapped.map { it.productId }}")
        _purchases.value = mapped
        // The PurchasesUpdatedListener only fires on NEW purchases — not for past
        // purchases recovered at startup. So sweep ack here too.
        autoAcknowledgePurchases(rawList)
        mapped
    }.onFailure { Log.e(TAG, "queryPurchases failed", it) }

    override suspend fun launchPurchase(activity: Activity, productId: String): Result<Unit> =
        runCatching {
            Log.i(TAG, "launchPurchase productId=$productId clientReady=${client.isReady}")
            if (!awaitReady()) error("launchPurchase blocked: status=${_status.value}")
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),
                    ),
                )
                .build()
            val productResult = client.queryProductDetails(params)
            val productBr = productResult.billingResult
            if (productBr.responseCode != BillingClient.BillingResponseCode.OK) {
                error("queryProductDetails failed code=${productBr.responseCode} debug=${productBr.debugMessage}")
            }
            val product = productResult.productDetailsList?.firstOrNull()
                ?: error("Product '$productId' not found in Play Console — did you create the product and is the app signed with the same keystore?")
            Log.i(TAG, "launchPurchase product loaded title='${product.title}'")
            val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product)
                .build()
            val billingResult = client.launchBillingFlow(
                activity,
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(listOf(productParams))
                    .build(),
            )
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                error("launchBillingFlow failed code=${billingResult.responseCode} debug=${billingResult.debugMessage}")
            }
            Log.i(TAG, "launchPurchase billing flow launched")
            Unit
        }.onFailure { Log.e(TAG, "launchPurchase failed", it) }

    override suspend fun acknowledge(purchaseToken: String): Result<Unit> = runCatching {
        Log.i(TAG, "acknowledge purchaseToken=${purchaseToken.take(8)}…")
        if (!awaitReady()) error("acknowledge blocked: status=${_status.value}")
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        val result: BillingResult = suspendCancellableCoroutine { cont ->
            client.acknowledgePurchase(params) { cont.resume(it) }
        }
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            error("acknowledge failed code=${result.responseCode} debug=${result.debugMessage}")
        }
        Log.i(TAG, "acknowledge ok")
        Unit
    }.onFailure { Log.e(TAG, "acknowledge failed", it) }
}
