package com.dvm.menu.category.presentation

import android.content.res.Configuration
import androidx.compose.animation.Animatable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.database.api.models.Subcategory
import com.dvm.drawer_api.Drawer
import com.dvm.menu.category.presentation.model.CategoryEvent
import com.dvm.menu.category.presentation.model.CategoryState
import com.dvm.menu.category.presentation.model.OrderType
import com.dvm.menu.common.ui.DishItem
import com.dvm.ui.components.Alert
import com.dvm.ui.components.AlertButton
import com.dvm.ui.components.AppBarIconBack
import com.dvm.ui.components.verticalGradient
import com.dvm.ui.themes.DecorColors
import com.dvm.utils.DrawerItem
import com.dvm.utils.asString
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars

private val AppBarHeight = 56.dp

@Composable
internal fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state: CategoryState = viewModel.state
    val context = LocalContext.current

    Drawer(selected = DrawerItem.MENU) {
        val lazyListState = rememberLazyListState()
        val titleHeight = remember { mutableStateOf(0) }
        var selectedColor by rememberSaveable {
            mutableStateOf(DecorColors.values().random())
        }
        val backgroundColor = MaterialTheme.colors.surface
        val animatableColor = remember { Animatable(backgroundColor) }
        val density = LocalDensity.current
        val appBarHeight = remember {
            with(density) { AppBarHeight.toPx() }.toInt()
        }
        val maxTabOffset = appBarHeight + titleHeight.value

        val offset = if (lazyListState.firstVisibleItemIndex == 0) {
            -lazyListState.firstVisibleItemScrollOffset.coerceAtMost(maxTabOffset)
        } else {
            -maxTabOffset
        }

        LaunchedEffect(selectedColor) {
            animatableColor.animateTo(
                targetValue = selectedColor.color,
                animationSpec = tween(durationMillis = 1000)
            )
        }

        Box {
            DishList(
                state = state,
                offset = offset,
                lazyListState = lazyListState,
                titleHeight = titleHeight,
                animatableColor = animatableColor,
                selectedColor = selectedColor.color,
                onDishClick = { viewModel.dispatch(CategoryEvent.OpenDish(it)) },
                onAddToCartClick = { dishId, dishName ->
                    viewModel.dispatch(CategoryEvent.AddToCart(dishId, dishName))
                }
            )

            CategoryAppBar(
                selectedOrder = state.orderType,
                selectedColor = selectedColor.color,
                offset = offset,
                modifier = Modifier.statusBarsPadding(),
                onBackClick = { viewModel.dispatch(CategoryEvent.Back) },
                onOrderClick = { viewModel.dispatch(CategoryEvent.OrderBy(it)) }
            )

            val subcategories = state.subcategories
            if (subcategories.isNotEmpty()) {
                Column {
                    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                    Spacer(Modifier.height(AppBarHeight))
                    val titleHeightDp = with(LocalDensity.current) { titleHeight.value.toDp() }
                    Spacer(Modifier.height(titleHeightDp))
                    val selectedTabIndex =
                        subcategories.indexOfFirst { it.id == state.selectedId }
                    SubcategoryTabs(
                        subcategories = subcategories,
                        selectedTabIndex = selectedTabIndex,
                        offset = offset,
                        onColorSelected = { selectedColor = it },
                        onSubcategoryClick = {
                            viewModel.dispatch(CategoryEvent.ChangeSubcategory(it))
                        }
                    )
                }
            }
        }

        state.alert?.let {
            Alert(
                message = state.alert.asString(context),
                onDismiss = { viewModel.dispatch(CategoryEvent.DismissAlert) }
            ) {
                AlertButton(onClick = { viewModel.dispatch(CategoryEvent.DismissAlert) })
            }
        }
    }
}

@Composable
private fun DishList(
    state: CategoryState,
    offset: Int,
    lazyListState: LazyListState,
    titleHeight: MutableState<Int>,
    selectedColor: Color,
    animatableColor: Animatable<Color, AnimationVector4D>,
    onDishClick: (dishId: String) -> Unit,
    onAddToCartClick: (dishId: String, dishName: String) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .verticalGradient(animatableColor.value.copy(alpha = 0.25f))
    ) {

        val configuration = LocalConfiguration.current

        val rows = when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 2
        }
        val itemWidth = with(LocalDensity.current) { (constraints.maxWidth / rows).toDp() - 5.dp }

        Crossfade(state.dishes) {
            LaunchedEffect(it) {
                lazyListState.scrollToItem(0, -offset)
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 5.dp)
            ) {
                item {
                    DishListHeader(
                        state = state,
                        titleHeight = titleHeight,
                        selectedColor = selectedColor
                    )
                }

                val chunkedDishes = state.dishes.chunked(rows)
                items(chunkedDishes) { dishes ->
                    Row(Modifier.fillMaxWidth()) {
                        dishes.fastForEach { dish ->
                            DishItem(
                                dish = dish,
                                modifier = Modifier
                                    .width(itemWidth)
                                    .padding(5.dp),
                                onDishClick = onDishClick,
                                onAddToCartClick = {
                                    onAddToCartClick(dish.id, dish.name)
                                }
                            )
                        }
                    }
                }
                item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)) }
            }
        }
    }
}

@Composable
private fun DishListHeader(
    state: CategoryState,
    selectedColor: Color,
    titleHeight: MutableState<Int>
) {
    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
    Spacer(Modifier.height(AppBarHeight))
    state.title?.let { title ->
        val context = LocalContext.current
        Text(
            text = title.asString(context),
            style = MaterialTheme.typography.h2,
            modifier = Modifier
                .onSizeChanged { size -> titleHeight.value = size.height }
                .padding(start = 20.dp, bottom = 15.dp)
        )
    }

    if (state.subcategories.isNotEmpty()) {
        Spacer(Modifier.height(AppBarHeight))
    } else {
        Divider(Modifier.padding(horizontal = 8.dp))
        Divider(
            color = selectedColor.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Divider(Modifier.padding(horizontal = 8.dp))
    }
    Spacer(Modifier.height(15.dp))
}

@Composable
private fun CategoryAppBar(
    selectedOrder: OrderType?,
    selectedColor: Color,
    offset: Int,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onOrderClick: (OrderType) -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            AppBarIconBack(
                modifier = Modifier
                    .graphicsLayer(
                        translationX = offset.toFloat(),
                        alpha = 1f + offset * 0.03f
                    ),
                onNavigateUp = onBackClick
            )
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        modifier = modifier,
        actions = {

            var expanded by remember { mutableStateOf(false) }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                OrderType.values().forEach { type ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onOrderClick(type)
                        }
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(type.text),
                                color = if (type == selectedOrder) {
                                    selectedColor
                                } else {
                                    Color.Unspecified
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 10.dp)
                            )
                            Text(
                                text = stringResource(type.sign),
                                color = if (type == selectedOrder) {
                                    selectedColor
                                } else {
                                    Color.Unspecified
                                }
                            )
                        }
                    }
                }
            }
            IconButton(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .graphicsLayer(
                        translationX = -offset.toFloat(),
                        alpha = 1f + offset * 0.03f
                    ),
                onClick = { expanded = true }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Sort,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun SubcategoryTabs(
    subcategories: List<Subcategory>,
    selectedTabIndex: Int,
    offset: Int,
    onColorSelected: (DecorColors) -> Unit,
    onSubcategoryClick: (subcategoryId: String) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        indicator = {},
        modifier = Modifier.graphicsLayer(translationY = offset.toFloat()),
        backgroundColor = MaterialTheme.colors.background
    ) {

        val colors = rememberSaveable { DecorColors.values().toList().shuffled() }

        subcategories.fastForEachIndexed { index, subcategory ->

            val color = remember { colors[index % colors.size] }
            val selected = index == selectedTabIndex
            if (selected) onColorSelected(color)

            Tab(
                selected = selected,
                onClick = { onSubcategoryClick(subcategory.id) }
            ) {
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = color.color.copy(alpha = 0.15f),
                    border = if (selected) {
                        BorderStroke(
                            width = 1.dp,
                            color = color.color.copy(alpha = 0.5f)
                        )
                    } else {
                        null
                    },
                    modifier = Modifier.padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    )
                ) {
                    Text(
                        text = subcategory.name,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }
        }
    }
}