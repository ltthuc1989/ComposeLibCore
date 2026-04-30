package com.trithuc.app.di

import com.ltthuc.billing.api.OrdersID
import com.ltthuc.utils.secrets.ISecretAdsKey
import com.ltthuc.utils.secrets.ISecretBillingKey
import com.trithuc.app.AppSecretAdsKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SecretsModule {

    @Binds
    @Singleton
    abstract fun bindAdsSecret(impl: AppSecretAdsKey): ISecretAdsKey

    companion object {
        @Provides
        @Singleton
        fun provideBillingSecret(): ISecretBillingKey = object : ISecretBillingKey {
            override val licenseKey: String = ""
            override val productIds: List<String> = listOf(OrdersID.REMOVE_ADS)
            override val subscriptionIds: List<String> = emptyList()
        }
    }
}
