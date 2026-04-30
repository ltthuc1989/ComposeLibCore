package com.ltthuc.ads

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.ltthuc.utils.secrets.ISecretAdsKey
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

enum class BannerType(val mode: String) {
    COLLAPSIBLE_TOP("top"),
    COLLAPSIBLE_BOTTOM("bottom"),
    ADAPTIVE_BANNER("adaptive"),
}

class BannerAd(private val secret: ISecretAdsKey) {

    private var adView: AdView? = null
    private var isExpanded = false
    private var isClear = false

    fun loadAndShowBannerAd(
        isAnchored: Boolean,
        container: FrameLayout,
        bannerType: BannerType = BannerType.ADAPTIVE_BANNER,
        onLoaded: () -> Unit = {},
        onFailed: () -> Unit = {},
    ) {
        if (AdsSettings.disableAd) return
        val context = container.context
        if (!context.isConnected) {
            onFailed()
            return
        }
        isClear = false

        val request: AdRequest = if (bannerType == BannerType.ADAPTIVE_BANNER) {
            adView = AdView(context).apply { adUnitId = secret.getBannerAdsID() }
            AdRequest.Builder().build()
        } else {
            adView = AdView(context).apply { adUnitId = secret.getBannerAdsID() }
            if (!isExpanded) {
                val extras = Bundle().apply { putString("collapsible", bannerType.mode) }
                AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                    .build()
            } else {
                AdRequest.Builder().build()
            }
        }

        adView?.adListener = object : AdListener() {
            override fun onAdClosed() {
                if (!isClear) isExpanded = true
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                container.visibility = View.GONE
                onFailed()
            }

            override fun onAdLoaded() {
                container.visibility = View.VISIBLE
                onLoaded()
            }
        }

        try {
            adView?.setAdSize(
                if (isAnchored) adSize(context.applicationContext, container) else AdSize.BANNER,
            )
        } catch (_: Exception) {
            adView?.setAdSize(AdSize.BANNER)
        }

        if (adView?.parent != null) {
            (adView?.parent as ViewGroup).removeView(adView)
            container.visibility = View.VISIBLE
        } else {
            adView?.loadAd(request)
        }
        container.addView(adView)
    }

    fun clear() {
        isClear = true
        (adView?.parent as? ViewGroup)?.removeView(adView)
        adView?.removeAllViews()
        adView?.destroy()
        adView = null
    }
}

internal fun adSize(context: Context, view: View): AdSize =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getSizeAboveAndroidApi30(context, view)
    } else {
        getSizeBelowAndroidApi30(context, view)
    }

@Suppress("DEPRECATION")
private fun getSizeBelowAndroidApi30(context: Context, view: View): AdSize {
    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val metrics = DisplayMetrics().also(display::getMetrics)
    val widthPx = if (view.width > 0) view.width.toFloat() else metrics.widthPixels.toFloat()
    val widthDp = (widthPx / metrics.density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
}

@RequiresApi(Build.VERSION_CODES.R)
private fun getSizeAboveAndroidApi30(context: Context, view: View): AdSize {
    val windowMetrics =
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
    val bounds = windowMetrics.bounds
    val widthPx = if (view.width > 0) view.width.toFloat() else bounds.width().toFloat()
    val widthDp = (widthPx / context.resources.displayMetrics.density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
}
