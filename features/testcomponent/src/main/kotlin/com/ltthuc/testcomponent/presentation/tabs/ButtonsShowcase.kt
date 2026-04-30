package com.ltthuc.testcomponent.presentation.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ltthuc.ui.components.AppButton
import com.ltthuc.ui.components.AppButtonVariant
import com.ltthuc.ui.themes.Dimens

@Composable
fun ButtonsShowcase(modifier: Modifier = Modifier) {
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.spacingM),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingM),
    ) {
        SectionLabel("Variants")
        AppButton(text = "Primary", onClick = {}, variant = AppButtonVariant.Primary)
        AppButton(text = "Secondary", onClick = {}, variant = AppButtonVariant.Secondary)
        AppButton(text = "Text", onClick = {}, variant = AppButtonVariant.Text)

        SectionLabel("With icon")
        AppButton(text = "Add", onClick = {}, leadingIcon = Icons.Default.Add)

        SectionLabel("States")
        AppButton(text = "Disabled", onClick = {}, enabled = false)
        AppButton(
            text = if (loading) "Loading…" else "Tap to load",
            onClick = { loading = !loading },
            loading = loading,
        )
    }
}

@Composable
internal fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}
