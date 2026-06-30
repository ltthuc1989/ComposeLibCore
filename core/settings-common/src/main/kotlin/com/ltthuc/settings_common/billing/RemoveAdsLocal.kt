package com.ltthuc.settings_common.billing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal that holds the app-scoped [RemoveAdsController]. Consumer
 * wraps content in [RemoveAdsControllerProvider] once at the root composable;
 * library composables (e.g. CommonPremiumSection) read from this.
 *
 * `staticCompositionLocalOf` because the controller is set once at startup
 * and never changes during the app's lifetime — no need for recomposition
 * tracking.
 */
val LocalRemoveAdsController = staticCompositionLocalOf<RemoveAdsController> {
    error(
        "No RemoveAdsController provided. Wrap your content in " +
            "RemoveAdsControllerProvider(controller = ...) at the root before " +
            "calling CommonPremiumSection() or any composable that reads it.",
    )
}

@Composable
fun RemoveAdsControllerProvider(
    controller: RemoveAdsController,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalRemoveAdsController provides controller, content = content)
}
