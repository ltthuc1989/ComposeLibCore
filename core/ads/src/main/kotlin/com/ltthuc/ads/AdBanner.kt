package com.ltthuc.ads

import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.ltthuc.utils.secrets.ISecretAdsKey

/**
 * Compose wrapper around [BannerAd]. Renders a native [AdView] inside a [FrameLayout] hosted by
 * [AndroidView]; banner cleared on dispose.
 */
@Composable
fun AdBanner(
    secret: ISecretAdsKey,
    modifier: Modifier = Modifier,
    bannerType: BannerType = BannerType.ADAPTIVE_BANNER,
    isAnchored: Boolean = true,
    onLoaded: () -> Unit = {},
    onFailed: () -> Unit = {},
) {
    val context = LocalContext.current
    val container = remember { FrameLayout(context) }
    val banner = remember(secret) { BannerAd(secret) }

    DisposableEffect(secret, bannerType, isAnchored) {
        if (secret.isAdsEnabled() && !AdsSettings.disableAd) {
            banner.loadAndShowBannerAd(
                isAnchored = isAnchored,
                container = container,
                bannerType = bannerType,
                onLoaded = onLoaded,
                onFailed = onFailed,
            )
        }
        onDispose { banner.clear() }
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { container },
    )
}
