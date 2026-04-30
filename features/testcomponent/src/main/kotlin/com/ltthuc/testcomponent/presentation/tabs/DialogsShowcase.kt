package com.ltthuc.testcomponent.presentation.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ltthuc.ui.components.AppButton
import com.ltthuc.ui.components.BaseBottomSheet
import com.ltthuc.ui.components.BaseDialog
import com.ltthuc.ui.themes.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogsShowcase(modifier: Modifier = Modifier) {
    var dialogVisible by remember { mutableStateOf(false) }
    var sheetVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.spacingM),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingM),
    ) {
        SectionLabel("AlertDialog")
        AppButton(
            text = "Show dialog",
            onClick = { dialogVisible = true },
        )

        SectionLabel("BottomSheet")
        AppButton(
            text = "Show bottom sheet",
            onClick = { sheetVisible = true },
        )

        if (dialogVisible) {
            BaseDialog(
                title = "Delete item?",
                message = "This action cannot be undone.",
                onDismiss = { dialogVisible = false },
                confirmText = "Delete",
                onConfirm = { dialogVisible = false },
                dismissText = "Cancel",
            )
        }
    }
    if (sheetVisible) {
        BaseBottomSheet(
            onDismissRequest = { sheetVisible = false },
            title = "Choose an option",
            sheetContent = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spacingS)) {
                    listOf("Profile", "Settings", "Sign out").forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.spacingS),
                        )
                    }
                }
            },
        )
    }
}
