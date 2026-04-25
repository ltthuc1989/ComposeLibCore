package com.dvm.menu.menu

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.drawer_api.Drawer
import com.dvm.menu.R
import com.dvm.menu.common.MENU_SPECIAL_OFFER
import com.dvm.menu.menu.model.MenuEvent
import com.dvm.menu.menu.model.MenuItem
import com.dvm.ui.components.AppBarIconMenu
import com.dvm.ui.themes.DecorColors
import com.dvm.utils.DrawerItem
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import kotlinx.coroutines.launch
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars

@Composable
internal fun MenuScreen(
    viewModel: MenuViewModel = hiltViewModel(),
) {
    val menuItems = viewModel.menuItems

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Drawer(
        drawerState = drawerState,
        selected = DrawerItem.MENU
    ) {
        Column {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            TopAppBar(
                title = { Text(stringResource(CoreR.string.menu_appbar_title)) },
                navigationIcon = {
                    AppBarIconMenu(
                        onAppMenuClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                actions = {
                    IconButton(
                        modifier = Modifier.padding(end = 12.dp),
                        onClick = { viewModel.dispatch(MenuEvent.Search) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null
                        )
                    }
                }
            )
            Divider()
            MenuContent(
                menuItems = menuItems,
                onItemClick = { viewModel.dispatch(MenuEvent.OpenMenuItem(it)) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MenuContent(
    menuItems: List<MenuItem>,
    onItemClick: (String) -> Unit,
) {
    val configuration = LocalConfiguration.current

    val rows = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 5
        else -> 3
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(rows),
        contentPadding = PaddingValues(
            top = 20.dp,
            start = 10.dp,
            end = 10.dp
        )
    ) {
        itemsIndexed(menuItems) { index, item ->
            MenuItem(
                index = index,
                item = item,
                modifier = Modifier.fillMaxWidth(),
                onItemClick = onItemClick
            )
        }
        items(rows) { Spacer(Modifier.navigationBarsPadding()) }
    }
}

@Composable
private fun MenuItem(
    index: Int,
    item: MenuItem,
    modifier: Modifier,
    onItemClick: (String) -> Unit,
) {
    val rowIndex = (index - index % 3) / 3
    val height = 128
    val startY = with(LocalDensity.current) { (-height * rowIndex).dp.toPx() }
    val endY = with(LocalDensity.current) { (height * 5 - height * rowIndex).dp.toPx() }

    Card(
        elevation = 3.dp,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                startY = startY,
                endY = endY,
                colors = listOf(
                    Color(0xFF9B4EF1),
                    Color(0xFF38B35B),
                    Color(0xFF3F51B5),
                ),
                tileMode = TileMode.Mirror
            )
        ),
        modifier = modifier
            .aspectRatio(1f)
            .padding(7.dp)
            .clickable(
                onClick = {
                    onItemClick(
                        when (item) {
                            is MenuItem.Item -> item.id
                            MenuItem.SpecialOffer -> MENU_SPECIAL_OFFER
                        }
                    )
                }
            )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Icon(
                    painter = painterResource(
                        id = when (item) {
                            is MenuItem.Item -> getIcon(item.title)
                            MenuItem.SpecialOffer -> R.drawable.icon_special_offer
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = DecorColors.DARK_BLUE.color.copy(alpha = 0.5f)
                )
            }
            Text(
                text = when (item) {
                    is MenuItem.Item -> item.title
                    MenuItem.SpecialOffer -> "Акции"
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(5.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

// It's not the right way, but this app is not for production
// and I don't like icons that comes from server
internal fun getIcon(dishTitle: String): Int {
    return when (dishTitle) {
        "Бургеры и хот-доги" -> R.drawable.icon_hamburger
        "Пицца" -> R.drawable.icon_pizza
        "Суши и роллы" -> R.drawable.icon_sushi
        "Завтраки" -> R.drawable.icon_breakfast
        "Супы" -> R.drawable.icon_soup
        "Основное блюдо" -> R.drawable.icon_main_dish
        "Гарниры" -> R.drawable.icon_side_dish
        "Паста" -> R.drawable.icon_pasta
        "Пельмени" -> R.drawable.icon_dumpling
        "Салаты" -> R.drawable.icon_vegetables
        "Десерты" -> R.drawable.icon_dessert
        "Закуски" -> R.drawable.icon_snack
        "Блюда на гриле" -> R.drawable.icon_grill
        "Соусы" -> R.drawable.icon_sauce
        "Напитки" -> R.drawable.icon_juice
        else -> R.drawable.icon_special_offer
    }
}