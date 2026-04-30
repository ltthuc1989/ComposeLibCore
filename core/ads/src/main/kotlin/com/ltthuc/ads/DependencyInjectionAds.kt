package com.ltthuc.ads

import android.app.Application
import android.content.Context
import com.ltthuc.utils.secrets.ISecretAdsKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DependencyInjectionAds {

    @Singleton
    @Provides
    fun providesAdsManager(
        @ApplicationContext context: Context,
        secret: ISecretAdsKey,
    ): AdsManager = AdsManager(context as Application, secret)

    @Singleton
    @Provides
    fun providesInterstitialAdsManager(
        @ApplicationContext context: Context,
        secret: ISecretAdsKey,
    ): InterstitialAdManager = InterstitialAdManager(context, secret)

    @Singleton
    @Provides
    fun providesAppOpenAdsManager(
        @ApplicationContext context: Context,
        secret: ISecretAdsKey,
    ): AppOpenAdsManager = AppOpenAdsManager(context as Application, secret)

    @Singleton
    @Provides
    fun providesRewardedAdsManager(
        @ApplicationContext context: Context,
        secret: ISecretAdsKey,
    ): RewardedAdsManager = RewardedAdsManager(context, secret)

    @Singleton
    @Provides
    fun providesGoogleMobileAdsConsentManager(
        @ApplicationContext context: Context,
    ): GoogleMobileAdsConsentManager = GoogleMobileAdsConsentManager(context)
}
