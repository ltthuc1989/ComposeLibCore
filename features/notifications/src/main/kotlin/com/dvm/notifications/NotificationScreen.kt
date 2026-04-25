package com.dvm.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.drawer_api.Drawer
import com.dvm.notifications.model.NotificationEvent
import com.dvm.notifications.model.NotificationState
import com.dvm.ui.components.AppBarIconMenu
import com.dvm.ui.components.DefaultAppBar
import com.dvm.ui.components.EmptyPlaceholder
import com.dvm.ui.components.verticalGradient
import com.dvm.ui.themes.DecorColors
import com.dvm.utils.DrawerItem
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import kotlinx.coroutines.launch
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars

@OptIn(ExperimentalComposeApi::class)
@Composable
internal fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val state: NotificationState = viewModel.state

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Drawer(
        drawerState = drawerState,
        selected = DrawerItem.NOTIFICATION
    ) {

        val color by rememberSaveable {
            mutableStateOf(DecorColors.values().random())
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalGradient(color.color.copy(alpha = 0.15f))
        ) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            DefaultAppBar(
                title = { Text(stringResource(CoreR.string.notification_appbar_title)) },
                navigationIcon = {
                    AppBarIconMenu {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                }
            )

            if (state.notifications.isEmpty()) {
                EmptyPlaceholder(
                    resId = com.dvm.ui.R.raw.empty_image,
                    text = stringResource(CoreR.string.notification_empty_placeholder),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val lazyListState = rememberLazyListState()

                val lastItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                LaunchedEffect(lastItem) {
                    lastItem?.let {
                        viewModel.dispatch(NotificationEvent.ChangeVisibleItem(lastItem))
                    }
                }

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 25.dp, top = 20.dp)
                        .navigationBarsPadding()
                ) {
                    items(state.notifications) { notification ->
                        NotificationItem(
                            title = notification.title,
                            text = notification.text,
                            seen = notification.seen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    title: String,
    text: String,
    seen: Boolean
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = title,
                color = if (!seen) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onSurface
                },
                modifier = Modifier.weight(1f)
            )
            if (!seen) {
                Text(
                    text = "⬤",
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
                text = text,
                modifier = Modifier.padding(end = 50.dp, bottom = 10.dp)
            )
        }
        Divider()
    }
}