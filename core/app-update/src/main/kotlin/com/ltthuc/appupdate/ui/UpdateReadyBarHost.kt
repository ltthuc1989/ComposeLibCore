package com.ltthuc.appupdate.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ltthuc.appupdate.api.AppUpdateEntryPoint
import dagger.hilt.android.EntryPointAccessors

/**
 * [UpdateReadyBar] that resolves the controller itself — the one-line counterpart to
 * `AppUpdateAutoInit.install(this)`:
 *
 * ```
 * UpdateReadyBarHost(
 *     colors = UpdateBarColors(
 *         surface = palette().cardSurface,
 *         text = palette().labelPrimary,
 *         action = palette().accent,
 *         track = palette().divider,
 *     ),
 * )
 * ```
 *
 * Exists so a consumer does not have to thread [com.ltthuc.appupdate.api.AppUpdateController]
 * through a root ViewModel it may not have: every app cloned from the template has a different
 * root — some hold a `RootViewModel`, some hand dependencies down from `MainActivity` — and the
 * glue was otherwise copy-pasted per app. An app that already exposes the controller from its own
 * root state holder can keep using [UpdateReadyBar] directly; both drive the same singleton.
 *
 * Place it once, high in the layout and always composed — a bar inside a screen that the user has
 * navigated away from is a downloaded update they never get to install.
 */
@Composable
fun UpdateReadyBarHost(colors: UpdateBarColors, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val controller = remember(context.applicationContext) {
        EntryPointAccessors
            .fromApplication(context.applicationContext, AppUpdateEntryPoint::class.java)
            .appUpdateController()
    }
    val state by controller.state.collectAsStateWithLifecycle()
    UpdateReadyBar(
        state = state,
        onRestart = controller::completeUpdate,
        colors = colors,
        modifier = modifier,
    )
}
