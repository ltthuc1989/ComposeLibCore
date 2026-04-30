package com.trithuc.app

import com.ltthuc.billing.api.BillingApi
import com.ltthuc.billing.api.OrdersID
import com.ltthuc.utils.secrets.ISecretAdsKey
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Singleton
class AppSecretAdsKey @Inject constructor(
    billingApi: BillingApi,
) : ISecretAdsKey {

    private val purchasedCache = AtomicBoolean(false)

    init {
        billingApi.purchases
            .onEach { list -> purchasedCache.set(list.any { it.productId == OrdersID.REMOVE_ADS }) }
            .launchIn(CoroutineScope(SupervisorJob() + Dispatchers.IO))
    }

    override fun getApplicationID(): String = BuildConfig.ADS_APPLICATION_ID
    override fun getBannerAdsID(): String = BuildConfig.ADS_BANNER_ID
    override fun getFullScreenAdsID(): String = BuildConfig.ADS_FULL_SCREEN_ID
    override fun getAppOpenAdsID(): String = BuildConfig.ADS_APP_OPEN_ID
    override fun getRewardAdsID(): String = BuildConfig.ADS_REWARD_ID
    override fun getNativeAdsID(): String = BuildConfig.ADS_NATIVE_ID
    override fun isAdsEnabled(): Boolean = !BuildConfig.FORCE_HIDE_ADS && !purchasedCache.get()
}
