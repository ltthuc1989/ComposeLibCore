package com.dvm.menu.main

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.database.api.models.CardDishDetails
import com.dvm.drawer_api.Drawer
import com.dvm.menu.R
import com.dvm.menu.common.ui.DishItem
import com.dvm.menu.main.model.MainEvent
import com.dvm.menu.search.model.MainState
import com.dvm.ui.components.Alert
import com.dvm.ui.components.AlertButton
import com.dvm.ui.components.AppBarIconMenu
import com.dvm.ui.components.DefaultAppBar
import com.dvm.ui.components.verticalGradient
import com.dvm.ui.themes.DecorColors
import com.dvm.utils.DrawerItem
import com.dvm.utils.asString
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import kotlinx.coroutines.launch
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars

@Composable
internal fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state: MainState = viewModel.state

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Drawer(
        drawerState = drawerState,
        selected = DrawerItem.MAIN
    ) {

        Column(Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            DefaultAppBar(
                navigationIcon = {
                    AppBarIconMenu {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = { viewModel.dispatch(MainEvent.OpenCart) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null
                    )
                }
            }

            val color by rememberSaveable {
                mutableStateOf(DecorColors.values().random())
            }

            BoxWithConstraints(
                Modifier
                    .fillMaxSize()
                    .verticalGradient(color.color.copy(alpha = 0.15f))
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Image(
                        painter = painterResource(R.drawable.cover),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                    Spacer(Modifier.height(15.dp))

                    val configuration = LocalConfiguration.current

                    val rows = when (configuration.orientation) {
                        Configuration.ORIENTATION_LANDSCAPE -> 4
                        else -> 2
                    }

                    val modifier =
                        Modifier
                            .width(this@BoxWithConstraints.maxWidth / (rows + 0.2).toFloat())

                    if (state.recommended.isNotEmpty()) {
                        DishesRowHeader(
                            text = stringResource(CoreR.string.main_recommended),
                            seeAllClick = { viewModel.dispatch(MainEvent.SeeAll) }
                        )
                        LazyRow {
                            items(state.recommended) { dish ->
                                MainDishItem(
                                    dish = dish,
                                    onEvent = { viewModel.dispatch(it) },
                                    modifier = modifier
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    if (state.best.isNotEmpty()) {
                        DishesRowHeader(
                            text = stringResource(CoreR.string.main_best),
                            seeAllClick = { viewModel.dispatch(MainEvent.SeeAll) }
                        )
                        LazyRow {
                            items(state.best) { dish ->
                                MainDishItem(
                                    dish = dish,
                                    onEvent = { viewModel.dispatch(it) },
                                    modifier = modifier
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    DishesRowHeader(
                        text = stringResource(CoreR.string.main_popular),
                        seeAllClick = { viewModel.dispatch(MainEvent.SeeAll) }
                    )
                    LazyRow {
                        items(state.popular) { dish ->
                            MainDishItem(
                                dish = dish,
                                onEvent = { viewModel.dispatch(it) },
                                modifier = modifier
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
    }

    state.alert?.let {
        Alert(
            message = state.alert.asString(context),
            onDismiss = { viewModel.dispatch(MainEvent.DismissAlert) }
        ) {
            AlertButton(onClick = { viewModel.dispatch(MainEvent.DismissAlert) })
        }
    }
}

@Composable
private fun MainDishItem(
    dish: CardDishDetails,
    modifier: Modifier,
    onEvent: (MainEvent) -> Unit
) {
    DishItem(
        dish = dish,
        modifier = modifier.padding(5.dp),
        onDishClick = { onEvent(MainEvent.OpenDish(dish.id)) },
        onAddToCartClick = { onEvent(MainEvent.AddToCart(dish.id, dish.name)) },
    )
}

@Composable
private fun DishesRowHeader(
    text: String,
    seeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = stringResource(CoreR.string.main_see_all),
            color = MaterialTheme.colors.primary,
            modifier = Modifier.clickable { seeAllClick() }
        )
    }
}