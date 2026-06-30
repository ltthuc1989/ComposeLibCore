package com.ltthuc.testcomponent.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ltthuc.ui.components.ios.IosSearchBar
import com.ltthuc.ui.components.ios.IosSegmented
import com.ltthuc.ui.components.ios.IosTextField
import com.ltthuc.ui.components.ios.iosCardModifier
import com.ltthuc.ui.themes.Dimens
import com.ltthuc.ui.themes.IosAppTheme
import com.ltthuc.ui.themes.IosColors
import com.ltthuc.ui.themes.LocalIosAppTheme

@Composable
fun IosShowcase(modifier: Modifier = Modifier) {
    var theme by remember { mutableStateOf(IosAppTheme.Default) }
    var unit by remember { mutableStateOf("Meters") }
    var width by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalIosAppTheme provides theme) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(IosColors.systemGroupedBackground),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.spacingM),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingM),
            ) {
                SectionLabel("IosAppTheme (drives accent color of inputs)")
                IosSegmented(
                    options = IosAppTheme.entries,
                    selected = theme,
                    onSelected = { theme = it },
                    label = { it.name },
                )

                SectionLabel("IosSegmented")
                IosSegmented(
                    options = listOf("Meters", "Yards", "Feet"),
                    selected = unit,
                    onSelected = { unit = it },
                    label = { it },
                )

                SectionLabel("IosTextField (decimal, right-aligned)")
                IosTextField(
                    value = width,
                    onValueChange = { width = it },
                    placeholder = "0.0",
                )

                SectionLabel("IosSearchBar")
                IosSearchBar(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Search calculators",
                )

                SectionLabel("Modifier.iosCardModifier()")
                Box(
                    modifier = Modifier
                        .iosCardModifier()
                        .padding(Dimens.spacingM),
                ) {
                    Text(
                        text = "iOS card — 16dp radius, soft shadow, white surface " +
                            "over the systemGroupedBackground page.",
                        color = IosColors.labelPrimary,
                    )
                }

                Box(modifier = Modifier.padding(top = 24.dp))
            }
        }
    }
}
