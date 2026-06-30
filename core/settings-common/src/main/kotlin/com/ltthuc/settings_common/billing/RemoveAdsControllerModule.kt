package com.ltthuc.settings_common.billing

import android.content.Context
import com.ltthuc.billing.api.BillingApi
import com.ltthuc.utils.secrets.ISecretBillingKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Provides the [RemoveAdsController] — the single owner of the Remove-Ads IAP flow (queries the
 * product, observes purchases, persists `hasRemovedAds`, mirrors `AdsSettings`).
 *
 * This binding lives in the library so consumer apps don't each copy the same Hilt boilerplate.
 * The Remove-Ads SKU is read from the app's [ISecretBillingKey.productIds] (first entry), falling
 * back to [RemoveAdsController.DEFAULT_PRODUCT_ID]. A consumer therefore only needs to:
 *  1. provide [ISecretBillingKey] (its SKU + license key) — typically a tiny `SecretsModule`; and
 *  2. wrap its UI in `RemoveAdsControllerProvider(controller = ...)` and call `CommonPremiumSection()`.
 *
 * [BillingApi] comes from template-billing's own Hilt module — no second billing connection.
 */
@Module
@InstallIn(SingletonComponent::class)
object RemoveAdsControllerModule {

    @Provides
    @Singleton
    fun provideRemoveAdsController(
        @ApplicationContext context: Context,
        billing: BillingApi,
        billingKey: ISecretBillingKey,
    ): RemoveAdsController {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val productId = billingKey.productIds.firstOrNull() ?: RemoveAdsController.DEFAULT_PRODUCT_ID
        return RemoveAdsController.create(
            context = context,
            scope = scope,
            billing = billing,
            productId = productId,
        )
    }
}
