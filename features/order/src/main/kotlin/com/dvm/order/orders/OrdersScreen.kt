package com.dvm.order.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.database.api.models.OrderData
import com.dvm.drawer_api.Drawer
import com.dvm.order.orders.model.OrderStatus
import com.dvm.order.orders.model.OrdersEvent
import com.dvm.order.orders.model.OrdersState
import com.dvm.ui.components.AppBarIconMenu
import com.dvm.ui.components.DefaultAppBar
import com.dvm.ui.components.EmptyPlaceholder
import com.dvm.ui.components.LoadingScrim
import com.dvm.ui.components.verticalGradient
import com.dvm.ui.themes.DecorColors
import com.dvm.utils.DrawerItem
import com.dvm.utils.extensions.format
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars

@Composable
internal fun OrdersScreen(
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val state: OrdersState = viewModel.state

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Drawer(
        drawerState = drawerState,
        selected = DrawerItem.ORDERS
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
                title = { Text(stringResource(CoreR.string.orders_appbar_title)) },
                navigationIcon = {
                    AppBarIconMenu {
                        scope.launch { drawerState.open() }
                    }
                },
            )

            val pagerState = rememberPagerState(pageCount = { 2 })

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = contentColorFor(MaterialTheme.colors.surface),
            ) {
                Tab(
                    text = { Text(stringResource(CoreR.string.orders_tab_actual)) },
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            viewModel.dispatch(OrdersEvent.StatusSelect(OrderStatus.ACTUAL))
                            pagerState.animateScrollToPage(0)
                        }
                    },
                )
                Tab(
                    text = { Text(stringResource(CoreR.string.orders_tab_completed)) },
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            viewModel.dispatch(OrdersEvent.StatusSelect(OrderStatus.COMPLETED))
                            pagerState.animateScrollToPage(1)
                        }
                    },
                )
            }
            HorizontalPager(state = pagerState) { page ->
                if (state.empty) {
                    EmptyPlaceholder(
                        resId = com.dvm.ui.R.raw.empty_image,
                        text = stringResource(CoreR.string.orders_empty_placeholder)
                    )
                } else {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(start = 30.dp)
                            .navigationBarsPadding()
                    ) {
                        items(state.orders) { order ->
                            OrderItem(
                                order = order,
                                onOrderClick = { viewModel.dispatch(OrdersEvent.Order(order.id)) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.progress) {
        LoadingScrim()
    }
}

@OptIn(ExperimentalComposeApi::class)
@Composable
private fun OrderItem(
    order: OrderData,
    onOrderClick: () -> Unit
) {
    Column(
        Modifier.clickable(onClick = onOrderClick)
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
            Text(
                text = order.createdAt.format(),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(top = 15.dp, bottom = 8.dp)
            )
        }
        Row {
            Text(
                text = stringResource(
                    CoreR.string.orders_order_number,
                    order.createdAt.time.toString().take(5)
                ),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colors.primary
            )
            Text(
                text = stringResource(
                    CoreR.string.dish_item_price,
                    order.total
                ),
                modifier = Modifier.padding(end = 8.dp),
                color = MaterialTheme.colors.primary
            )
        }
        Row {
            Text(
                text = order.address,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = order.status,
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            )
        }
        Divider()
    }
}