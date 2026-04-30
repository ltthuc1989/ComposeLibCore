package com.ltthuc.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ltthuc.ui.components.LoadingScrim
import com.ltthuc.ui.extensions.showError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.collectAsState

private val emptyProgressFlow: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()

@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: BaseViewModel? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    handleNavigationBarPadding: Boolean = true,
    handleImePadding: Boolean = true,
    content: @Composable (PaddingValues) -> Unit,
) {
    val progress by (viewModel?.progress ?: emptyProgressFlow).collectAsState()

    LaunchedEffect(viewModel) {
        viewModel?.errorMessage?.collect { error ->
            snackbarHostState.showError(error)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = backgroundColor,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .let {
                    if (handleNavigationBarPadding) {
                        it.windowInsetsPadding(WindowInsets.navigationBars)
                    } else {
                        it
                    }
                }
                .let {
                    if (handleImePadding) {
                        it.windowInsetsPadding(WindowInsets.ime)
                    } else {
                        it
                    }
                },
        ) {
            content(padding)
            if (progress) {
                LoadingScrim()
            }
        }
    }
}
