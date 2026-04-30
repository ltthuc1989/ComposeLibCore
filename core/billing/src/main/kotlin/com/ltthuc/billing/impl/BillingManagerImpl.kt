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
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine

private const val TAG = "BillingManager"

@Singleton
internal class BillingManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val secrets: ISecretBillingKey,
) : BillingApi {

    private val _status = MutableStateFlow<BillingStatus>(BillingStatus.Disconnected)
    override val status: StateFlow<BillingStatus> = _status.asStateFlow()

    private val _purchases = MutableStateFlow<List<PurchaseInfo>>(emptyList())
    override val purchases: StateFlow<List<PurchaseInfo>> = _purchases.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, list ->
        Log.i(
            TAG,
            "purchasesUpdated responseCode=${result.responseCode} debug=${result.debugMessage} count=${list?.size ?: 0}",
        )
        if (list != null) _purchases.update { list.flatMap(Purchase::toInfo) }
    }

    private val client: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build(),
        )
        .build()

    override fun connect() {
        if (client.isReady) {
            Log.i(TAG, "connect() already ready")
            _status.value = BillingStatus.Ready
            return
        }
        Log.i(TAG, "connect() startConnection")
        _status.value = BillingStatus.Connecting
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "onBillingSetupFinished READY")
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
                Log.w(TAG, "onBillingServiceDisconnected")
                _status.value = BillingStatus.Disconnected
            }
        })
    }

    override fun release() {
        Log.i(TAG, "release()")
        if (client.isReady) client.endConnection()
        _status.value = BillingStatus.Disconnected
    }

    override suspend fun queryProducts(productIds: List<String>): Result<List<ProductInfo>> =
        runCatching {
            val ids = productIds.ifEmpty { secrets.productIds }
            Log.i(TAG, "queryProducts ids=$ids")
            if (ids.isEmpty()) {
                Log.w(TAG, "queryProducts called with empty ids — nothing to query")
                return@runCatching emptyList()
            }
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
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val result = client.queryPurchasesAsync(params)
        val br = result.billingResult
        if (br.responseCode != BillingClient.BillingResponseCode.OK) {
            error("queryPurchases failed code=${br.responseCode} debug=${br.debugMessage}")
        }
        val mapped = result.purchasesList.flatMap(Purchase::toInfo)
        Log.i(TAG, "queryPurchases ok count=${mapped.size} ids=${mapped.map { it.productId }}")
        _purchases.value = mapped
        mapped
    }.onFailure { Log.e(TAG, "queryPurchases failed", it) }

    override suspend fun launchPurchase(activity: Activity, productId: String): Result<Unit> =
        runCatching {
            Log.i(TAG, "launchPurchase productId=$productId clientReady=${client.isReady}")
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
