package com.ltthuc.settings_common.billing

import android.app.Activity
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.ltthuc.ads.AdsSettings
import com.ltthuc.billing.api.BillingApi
import com.ltthuc.billing.api.model.ProductInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Owns the Remove-Ads IAP flow end-to-end so consumer apps don't repeat the
 * boilerplate per clone. Lifecycle:
 *
 *  1. On [create]: constructs the controller, the controller's `init` calls
 *     `billing.connect()` + `billing.queryProducts(listOf(productId))` (with
 *     [PRODUCT_QUERY_TIMEOUT_MS] safety net) + `billing.queryPurchases()`.
 *  2. Observes `billing.purchases`. When a purchase with [productId] is seen,
 *     emits `hasRemovedAds = true`, persists to internal DataStore so cold
 *     starts are instant, AND auto-mirrors to AdsSettings.disableAd / disableOpenAds.
 *  3. [launchPurchase] / [restorePurchases] just forward to BillingApi.
 *
 * Consumer wiring (the controller itself is provided by [RemoveAdsControllerModule] — apps no
 * longer write a `@Provides` for it):
 *   – provide [com.ltthuc.utils.secrets.ISecretBillingKey] with the Remove-Ads SKU as the first
 *     `productIds` entry (a tiny `SecretsModule`).
 *   – 1 `RemoveAdsControllerProvider(controller = ...) { ... }` wrap at the root composable.
 *   – 1 `CommonPremiumSection()` call inside Settings.
 */
interface RemoveAdsController {
    /** True once a purchase with the configured productId is observed. Backed by
     *  internal DataStore so cold-start value is instant (no ad flicker before
     *  BillingApi.connect completes). */
    val hasRemovedAds: StateFlow<Boolean>

    /** Localized product info (price label, currency, etc.) from
     *  `BillingApi.queryProducts`. Null until queried; null also after timeout if
     *  Play billing returns nothing. */
    val productInfo: StateFlow<ProductInfo?>

    /** Becomes true after the first `queryProducts` returns OR after
     *  [PRODUCT_QUERY_TIMEOUT_MS]. UI checks `(attempted && productInfo == null &&
     *  !hasRemovedAds)` to decide whether to hide the Premium section instead of
     *  showing "Loading…" forever on offline / missing-SKU setups. */
    val productLoadAttempted: StateFlow<Boolean>

    fun launchPurchase(activity: Activity)
    fun restorePurchases()

    /** Releases the underlying BillingApi connection. Call when the host scope
     *  is torn down (typically not needed for a Singleton — process death does it). */
    fun release()

    companion object {
        const val DEFAULT_PRODUCT_ID = "products_remove_ads"
        const val PRODUCT_QUERY_TIMEOUT_MS = 12_000L

        fun create(
            context: Context,
            scope: CoroutineScope,
            billing: BillingApi,
            productId: String = DEFAULT_PRODUCT_ID,
        ): RemoveAdsController = RemoveAdsControllerImpl(
            context = context.applicationContext,
            scope = scope,
            billing = billing,
            productId = productId,
        )
    }
}

private const val PREFS_NAME = "settings_common_remove_ads"
private val Context.removeAdsStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)
private val keyHasRemovedAds = booleanPreferencesKey("has_removed_ads")

private class RemoveAdsControllerImpl(
    private val context: Context,
    private val scope: CoroutineScope,
    private val billing: BillingApi,
    private val productId: String,
) : RemoveAdsController {

    private val _productInfo = MutableStateFlow<ProductInfo?>(null)
    override val productInfo: StateFlow<ProductInfo?> = _productInfo.asStateFlow()

    private val _productLoadAttempted = MutableStateFlow(false)
    override val productLoadAttempted: StateFlow<Boolean> = _productLoadAttempted.asStateFlow()

    // hasRemovedAds: seeded false (replaced async by DataStore's first emission).
    // Eagerly so AdsSettings mirror downstream sees the persisted value as soon
    // as the DataStore read completes, even before BillingApi.connect emits.
    override val hasRemovedAds: StateFlow<Boolean> =
        context.removeAdsStore.data
            .map { it[keyHasRemovedAds] == true }
            .stateIn(scope, SharingStarted.Eagerly, false)

    init {
        // 1. Auto-mirror to AdsSettings (every consumer expects this; AdsSettings
        //    is a process-global from template-ads).
        hasRemovedAds
            .onEach { removed ->
                AdsSettings.disableAd = removed
                AdsSettings.disableOpenAds = removed
            }
            .launchIn(scope)

        // 2. Connect billing + query products with a hard timeout safety net.
        billing.connect()
        scope.launch {
            val result = withTimeoutOrNull(RemoveAdsController.PRODUCT_QUERY_TIMEOUT_MS) {
                billing.queryProducts(listOf(productId))
            }
            result?.onSuccess { list -> _productInfo.value = list.firstOrNull() }
            _productLoadAttempted.value = true
        }
        scope.launch { billing.queryPurchases() }

        // 3. When a purchase with our productId is observed, persist hasRemovedAds.
        //    The DataStore write flows back into [hasRemovedAds] StateFlow above,
        //    which triggers the AdsSettings mirror.
        billing.purchases
            .onEach { list ->
                if (list.any { it.productId == productId }) {
                    persistHasRemovedAds(true)
                }
            }
            .launchIn(scope)
    }

    private suspend fun persistHasRemovedAds(value: Boolean) {
        context.removeAdsStore.edit { it[keyHasRemovedAds] = value }
    }

    override fun launchPurchase(activity: Activity) {
        // Suppress the App Open ad on return from Play billing UI — same rule as
        // CLAUDE.md §6.12 for Share / Rate / Feedback. AppOpenAdsManager consumes
        // this flag (sets it back to false) the first time it skips a show, so
        // it's a one-shot suppression covering exactly this user-initiated trip.
        AdsSettings.isOtherAppShowing = true
        scope.launch { billing.launchPurchase(activity, productId) }
    }

    override fun restorePurchases() {
        // Restore can open a Play UI on some devices (account chooser, etc.) —
        // suppress App Open ad on return.
        AdsSettings.isOtherAppShowing = true
        scope.launch {
            billing.queryPurchases()
            // If after restore there are NO purchases, flip back to false. iOS-parity:
            // user uninstalled / refunded should re-enable ads after restore.
            val purchases = billing.purchases.first()
            if (purchases.none { it.productId == productId }) {
                persistHasRemovedAds(false)
            }
        }
    }

    override fun release() {
        billing.release()
    }
}
