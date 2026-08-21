package com.ltthuc.appupdate.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ltthuc.appupdate.R
import com.ltthuc.appupdate.api.AppUpdateState

/**
 * Colours for [UpdateReadyBar]. Parameters rather than a theme lookup, so a consumer with its own
 * palette — light-only iOS system colours included — can hand its real values through.
 */
@Immutable
data class UpdateBarColors(
    val surface: Color,
    val text: Color,
    val action: Color,
    val track: Color,
)

/**
 * The visible half of a flexible update. Place it once, high in the app's layout (a `Box` over the
 * NavHost, aligned to the bottom) and give it the controller's state.
 *
 * Showing `ReadyToInstall` is not optional decoration: a flexible update installs on restart, so an
 * app that never surfaces it leaves a downloaded APK the user paid for and never receives.
 * `Downloading` is shown too, quietly — it explains the data usage the user did not ask about.
 */
@Composable
fun UpdateReadyBar(
    state: AppUpdateState,
    onRestart: () -> Unit,
    colors: UpdateBarColors,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = state !is AppUpdateState.Idle,
        modifier = modifier,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surface),
        ) {
            when (state) {
                is AppUpdateState.Downloading -> Downloading(state, colors)
                AppUpdateState.ReadyToInstall -> ReadyToInstall(onRestart, colors)
                AppUpdateState.Idle -> Unit
            }
        }
    }
}

@Composable
private fun Downloading(state: AppUpdateState.Downloading, colors: UpdateBarColors) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(
            text = stringResource(R.string.app_update_downloading),
            color = colors.text,
            fontSize = 14.sp,
        )
        val fraction = state.fraction
        val progressModifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .align(Alignment.BottomStart)
        // Play reports no total until the download actually starts; an indeterminate bar is honest
        // there, a 0% determinate one reads as stalled.
        if (fraction == null) {
            LinearProgressIndicator(
                modifier = progressModifier,
                color = colors.action,
                trackColor = colors.track,
            )
        } else {
            LinearProgressIndicator(
                progress = { fraction },
                modifier = progressModifier,
                color = colors.action,
                trackColor = colors.track,
            )
        }
    }
}

@Composable
private fun ReadyToInstall(onRestart: () -> Unit, colors: UpdateBarColors) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.app_update_ready),
            color = colors.text,
            fontSize = 14.sp,
            modifier = Modifier.padding(end = 12.dp),
        )
        Text(
            text = stringResource(R.string.app_update_restart),
            color = colors.action,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onRestart)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}
