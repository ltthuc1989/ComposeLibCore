package com.ltthuc.testcomponent.presentation.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ltthuc.ui.components.AppCard
import com.ltthuc.ui.components.AppCardVariant
import com.ltthuc.ui.components.EmptyState
import com.ltthuc.ui.components.ErrorState
import com.ltthuc.ui.themes.Dimens

@Composable
fun CardsShowcase(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.spacingM),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingM),
    ) {
        SectionLabel("Card variants")
        AppCard(variant = AppCardVariant.Filled, modifier = Modifier.fillMaxWidth()) {
            Text("Filled card", style = MaterialTheme.typography.titleSmall)
        }
        AppCard(variant = AppCardVariant.Outlined, modifier = Modifier.fillMaxWidth()) {
            Text("Outlined card", style = MaterialTheme.typography.titleSmall)
        }
        AppCard(variant = AppCardVariant.Elevated, modifier = Modifier.fillMaxWidth()) {
            Text("Elevated card", style = MaterialTheme.typography.titleSmall)
        }

        SectionLabel("Empty state")
        AppCard(modifier = Modifier.fillMaxWidth().height(260.dp), contentPadding = 0.dp) {
            EmptyState(title = "No items", description = "Add an item to see it here.")
        }

        SectionLabel("Error state")
        AppCard(modifier = Modifier.fillMaxWidth().height(280.dp), contentPadding = 0.dp) {
            ErrorState(
                title = "Failed to load",
                description = "Check your connection and try again.",
                onRetry = {},
            )
        }
    }
}
