package com.ltthuc.testcomponent.presentation.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ltthuc.ui.base.AppToolbarLarge
import com.ltthuc.ui.base.AppToolbarSearch
import com.ltthuc.ui.base.AppToolbarSmall
import com.ltthuc.ui.base.AppToolbarTabs
import com.ltthuc.ui.base.TabItem
import com.ltthuc.ui.base.ToolbarAction
import com.ltthuc.ui.themes.Dimens

@Composable
fun ToolbarsShowcase(modifier: Modifier = Modifier) {
    val actions = listOf(
        ToolbarAction(Icons.Default.Favorite, "Favorite", onClick = {}),
        ToolbarAction(Icons.Default.Share, "Share", onClick = {}),
    )
    var query by remember { mutableStateOf("") }
    var nestedTab by remember { mutableStateOf(0) }
    val scroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingM),
    ) {
        SectionLabelPadded("Small")
        AppToolbarSmall(
            title = "Title",
            actions = actions,
            onBack = {},
        )

        SectionLabelPadded("Large (collapses with vertical scroll)")
        AppToolbarLarge(
            title = "Large title",
            scrollState = scroll,
            actions = actions,
            onBack = {},
        )

        SectionLabelPadded("Search (live debounced)")
        AppToolbarSearch(
            placeholder = "Search…",
            initialQuery = query,
            onQueryChanged = { query = it },
            onBack = {},
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingM),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text("Last query: \"$query\"", style = MaterialTheme.typography.bodyMedium)
        }

        SectionLabelPadded("Tabs (nested)")
        AppToolbarTabs(
            title = "Sections",
            tabs = listOf(TabItem("Overview"), TabItem("Activity"), TabItem("Files"), TabItem("Settings")),
            selectedIndex = nestedTab,
            onTabSelected = { nestedTab = it },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(Dimens.spacingM),
            contentAlignment = Alignment.Center,
        ) {
            Text("Selected tab #$nestedTab", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun SectionLabelPadded(text: String) {
    Box(modifier = Modifier.padding(start = Dimens.spacingM, top = Dimens.spacingM)) {
        SectionLabel(text)
    }
}
