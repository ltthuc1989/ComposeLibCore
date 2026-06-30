package com.ltthuc.testcomponent.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ltthuc.testcomponent.presentation.model.TestComponentEvent
import com.ltthuc.testcomponent.presentation.tabs.BottomNavShowcase
import com.ltthuc.testcomponent.presentation.tabs.ButtonsShowcase
import com.ltthuc.testcomponent.presentation.tabs.CardsShowcase
import com.ltthuc.testcomponent.presentation.tabs.DialogsShowcase
import com.ltthuc.testcomponent.presentation.tabs.InputsShowcase
import com.ltthuc.testcomponent.presentation.tabs.IosShowcase
import com.ltthuc.testcomponent.presentation.tabs.ToolbarsShowcase

@Composable
fun TestComponentScreen(
    modifier: Modifier = Modifier,
    viewModel: TestComponentViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val tabs = remember {
        listOf("Buttons", "Inputs", "Dialogs", "Cards", "Toolbars", "Bottom Nav", "iOS")
    }

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = state.selectedTab,
            edgePadding = 0.dp,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTab == index,
                    onClick = { viewModel.dispatch(TestComponentEvent.SelectTab(index)) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                )
            }
        }
        when (state.selectedTab) {
            0 -> ButtonsShowcase()
            1 -> InputsShowcase()
            2 -> DialogsShowcase()
            3 -> CardsShowcase()
            4 -> ToolbarsShowcase()
            5 -> BottomNavShowcase()
            6 -> IosShowcase()
        }
    }
}
