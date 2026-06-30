package com.ltthuc.ads

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class AdsSettings {
    companion object {
        // Default false: apps without a splash screen don't need to manage this flag.
        // Apps WITH a splash must set true in splash.onStart and false in splash.onStop
        // (otherwise App Open ad will overlap the splash UI).
        var isSplashScreen = false
        var isInterstitialShowing = false
        var isRewardAdsShowing = false
        var adPos: Int = 0
        var disableAd = false
        var disableOpenAds = false
        var isOtherAppShowing = false

        fun addDeviceTest(context: Context) {
            if (BuildConfig.DEBUG) {
                val deviceId = getDeviceIdForAdMobTestAds(context) ?: return
                val configuration = RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(deviceId))
                    .build()
                MobileAds.setRequestConfiguration(configuration)
            }
        }

        fun disableAd(disable: Boolean) {
            disableAd = disable
        }

        @SuppressLint("HardwareIds")
        private fun getDeviceIdForAdMobTestAds(context: Context): String? {
            val androidId = Settings.Secure.getString(
                context.contentResolver, Settings.Secure.ANDROID_ID,
            ) ?: return null
            return try {
                val md = MessageDigest.getInstance("MD5")
                val array = md.digest(androidId.toByteArray())
                buildString {
                    array.forEach { byte ->
                        append(Integer.toHexString(byte.toInt() and 0xFF or 0x100).substring(1, 3))
                    }
                }
            } catch (_: NoSuchAlgorithmException) {
                null
            }
        }
    }
}

val Context.isConnected: Boolean
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }
