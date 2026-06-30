package com.ltthuc.rating.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.ltthuc.rating.api.RateHelper

@Composable
fun RateDialogHost(
    helper: RateHelper,
    title: String = "Rate & Review",
    message: String = "Your feedback is very important to us. We'd love to get your feedback on Google Play.",
    rateButton: String = "RATE NOW!",
    laterButton: String = "REMIND LATER",
) {
    val show by helper.showCustomDialog.collectAsState()
    if (!show) return
    val activity = LocalContext.current.findActivity() ?: return
    AlertDialog(
        onDismissRequest = { helper.onLater() },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = { helper.onRateNow(activity) }) { Text(rateButton) }
        },
        dismissButton = {
            TextButton(onClick = { helper.onLater() }) { Text(laterButton) }
        },
    )
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
