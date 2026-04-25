package com.dvm.menu.favorite

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.DrawerValue
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.drawer_api.Drawer
import com.dvm.menu.common.ui.DishItem
import com.dvm.menu.favorite.model.FavoriteEvent
import com.dvm.menu.favorite.model.FavoriteState
import com.dvm.ui.components.Alert
import com.dvm.ui.components.AlertButton
import com.dvm.ui.components.AppBarIconMenu
import com.dvm.ui.components.DefaultAppBar
import com.dvm.ui.components.EmptyPlaceholder
import com.dvm.ui.components.verticalGradient
import com.dvm.ui.themes.DecorColors
import com.dvm.utils.DrawerItem
import com.dvm.utils.asString
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import kotlinx.coroutines.launch
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FavoriteScreen(
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val state: FavoriteState = viewModel.state

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Drawer(
        drawerState = drawerState,
        selected = DrawerItem.FAVORITE
    ) {

        val color by rememberSaveable {
            mutableStateOf(DecorColors.values().random())
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalGradient(color.color.copy(alpha = 0.15f))
        ) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            DefaultAppBar(
                title = { Text(stringResource(CoreR.string.favorite_appbar_title)) },
                navigationIcon = {
                    AppBarIconMenu {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                }
            )

            if (state.dishes.isEmpty()) {
                EmptyPlaceholder(
                    resId = com.dvm.ui.R.raw.empty_image,
                    text = stringResource(CoreR.string.favorite_empty_placeholder),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val configuration = LocalConfiguration.current

                val rows = when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> 4
                    else -> 2
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(rows),
                    modifier = Modifier.padding(5.dp)
                ) {
                    items(state.dishes) { dish ->
                        DishItem(
                            dish = dish,
                            modifier = Modifier.padding(5.dp),
                            onDishClick = { viewModel.dispatch(FavoriteEvent.OpenDish(it)) },
                            onAddToCartClick = {
                                viewModel.dispatch(
                                    FavoriteEvent.AddToCart(
                                        dishId = dish.id,
                                        name = dish.name
                                    )
                                )
                            },
                        )
                    }
                    items(2) { Spacer(Modifier.navigationBarsPadding()) }
                }
            }
        }
    }

    state.alert?.let {
        Alert(
            message = state.alert.asString(context),
            onDismiss = { viewModel.dispatch(FavoriteEvent.DismissAlert) }
        ) {
            AlertButton(onClick = { viewModel.dispatch(FavoriteEvent.DismissAlert) })
        }
    }
}
