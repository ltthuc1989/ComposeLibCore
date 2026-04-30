package com.ltthuc.utils.secrets

interface ISecretAdsKey {
    fun getApplicationID(): String
    fun getBannerAdsID(): String
    fun getFullScreenAdsID(): String
    fun getAppOpenAdsID(): String
    fun getRewardAdsID(): String
    fun getNativeAdsID(): String
    fun isAdsEnabled(): Boolean
}
