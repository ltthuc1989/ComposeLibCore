package com.ltthuc.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.ltthuc.ui.themes.Dimens

@Composable
fun BaseDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String = "OK",
    onConfirm: () -> Unit = onDismiss,
    dismissText: String? = null,
    onCancel: () -> Unit = onDismiss,
    properties: DialogProperties = DialogProperties(),
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            AppButton(
                text = confirmText,
                onClick = onConfirm,
                variant = AppButtonVariant.Text,
            )
        },
        dismissButton = dismissText?.let {
            {
                AppButton(
                    text = it,
                    onClick = onCancel,
                    variant = AppButtonVariant.Text,
                )
            }
        },
        shape = RoundedCornerShape(Dimens.radiusM),
        properties = properties,
    )
}
