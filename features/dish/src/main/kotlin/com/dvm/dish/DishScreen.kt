package com.dvm.dish

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastMap
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.dish.model.DishEvent
import com.dvm.drawer_api.Drawer
import com.dvm.ui.components.Alert
import com.dvm.ui.components.AlertButton
import com.dvm.ui.components.AppBarIconBack
import com.dvm.ui.components.DefaultAppBar
import com.dvm.ui.components.ErrorImage
import com.dvm.ui.components.Image
import com.dvm.ui.themes.DecorColors
import com.dvm.utils.DrawerItem
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import com.dvm.ui.R as CoreR
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars

private const val HORIZONTAL_POINT_OFFSET = 50f
private const val VERTICAL_POINT_OFFSET = 50f

@OptIn(ExperimentalStdlibApi::class, ExperimentalComposeApi::class)
@Composable
internal fun DishScreen(
    viewModel: DishViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Drawer(selected = DrawerItem.NONE) {

        val dish = state.dish ?: return@Drawer
        val color = remember { DecorColors.values().random().color }
        val lazyListState = rememberLazyListState()
        val offset = lazyListState.firstVisibleItemScrollOffset

        TopGraphicHeader(
            lazyListState = lazyListState,
            color = color
        )

        LazyColumn(state = lazyListState) {
            item {
                Column(Modifier.fillMaxSize()) {

                    BottomGraphicHeader(
                        image = dish.image,
                        color = color,
                        offset = offset
                    )

                    Description(
                        name = dish.name,
                        description = dish.description
                    )

                    PurchaseSection(
                        price = dish.price,
                        oldPrice = dish.oldPrice,
                        quantity = state.quantity,
                        onRemoveItem = { viewModel.dispatch(DishEvent.RemovePiece) },
                        onAddItem = { viewModel.dispatch(DishEvent.AddPiece) },
                        onAddToCart = { viewModel.dispatch(DishEvent.AddToCart) },
                    )

                    Divider(Modifier.padding(top = 20.dp))

                    ReviewHeader(
                        rating = dish.rating,
                        color = color,
                        onAddReviewClick = { viewModel.dispatch(DishEvent.AddReview) }
                    )
                }
            }

            items(dish.reviews) { review ->
                ReviewItem(
                    review = review,
                    color = color
                )
            }

            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }

        Column {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            DishAppBar(
                color = color,
                isFavorite = dish.isFavorite,
                lazyListState = lazyListState,
                onNavigateUp = { viewModel.dispatch(DishEvent.Back) },
                onFavoriteClick = { viewModel.dispatch(DishEvent.ToggleFavorite) }
            )
        }

        if (state.reviewDialog) {
            ReviewDialog(
                onDismiss = { viewModel.dispatch(DishEvent.DismissReviewDialog) },
                onAddReview = { rating, text ->
                    viewModel.dispatch(
                        DishEvent.SendReview(
                            rating = rating,
                            text = text
                        )
                    )
                },
                networkCall = state.progress
            )
        }
    }

    state.alert?.let {
        Alert(
            message = stringResource(state.alert),
            onDismiss = { viewModel.dispatch(DishEvent.DismissAlert) }
        ) {
            AlertButton(onClick = { viewModel.dispatch(DishEvent.DismissAlert) })
        }
    }
}

@Composable
private fun Description(
    name: String,
    description: String?
) {
    Text(
        text = name,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 50.dp, bottom = 40.dp),
        style = MaterialTheme.typography.h4,
        textAlign = TextAlign.Center
    )

    Text(
        text = description.orEmpty(),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
    )
}

@Composable
private fun BottomGraphicHeader(
    image: String,
    color: Color,
    offset: Int
) {
    Spacer(
        Modifier
            .windowInsetsTopHeight(WindowInsets.statusBars)
            .background(color.copy(alpha = 0.3f))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 90.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            data = image,
            modifier = Modifier
                .size(280.dp)
                .border(
                    width = 1.dp,
                    color = Color.Gray
                ),
            error = {
                ErrorImage(Modifier.padding(top = 30.dp))
            }
        )

        Canvas(Modifier.fillMaxWidth()) {
            pointGrid(
                startY = 360f,
                lines = 2,
                verticalOffset = VERTICAL_POINT_OFFSET,
                horizontalOffset = HORIZONTAL_POINT_OFFSET,
                color = color,
                width = size.width,
                translationOffset = offset,
                movingLines = listOf(1 to Direction.RIGHT)
            )
        }
    }
}

@Composable
private fun TopGraphicHeader(
    lazyListState: LazyListState,
    color: Color
) {
    Column {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Canvas(Modifier.fillMaxWidth()) {

            val noTranslationOffset = 670
            val scrollOffset = lazyListState.firstVisibleItemScrollOffset

            val verticalTranslation = if (lazyListState.firstVisibleItemIndex == 0) {
                if (scrollOffset < noTranslationOffset) {
                    0
                } else {
                    noTranslationOffset - scrollOffset
                }
            } else {
                -450
            }

            drawRect(
                color = color.copy(alpha = 0.3f),
                topLeft = Offset(x = 0f, y = verticalTranslation.toFloat()),
                size = Size(width = size.width, height = 450f)
            )

            pointGrid(
                startY = 170f + verticalTranslation,
                lines = 3,
                color = color,
                width = size.width,
                verticalOffset = VERTICAL_POINT_OFFSET,
                horizontalOffset = HORIZONTAL_POINT_OFFSET,
                translationOffset = scrollOffset,
                movingLines = listOf(0 to Direction.RIGHT, 2 to Direction.LEFT)
            )
        }
    }
}

@Composable
private fun DishAppBar(
    isFavorite: Boolean,
    color: Color,
    lazyListState: LazyListState,
    onNavigateUp: () -> Unit,
    onFavoriteClick: () -> Unit
) {

    val scrollOffset = lazyListState.firstVisibleItemScrollOffset
    val iconsAlpha = 1f - scrollOffset * 0.01f

    val offset = if (lazyListState.firstVisibleItemIndex == 0) {
        scrollOffset
    } else {
        150
    }

    DefaultAppBar(
        navigationIcon = {
            AppBarIconBack(
                modifier = Modifier
                    .graphicsLayer(
                        translationX = -offset.toFloat(),
                        alpha = iconsAlpha
                    ),
                onNavigateUp = onNavigateUp
            )
        },
        actions = {
            IconButton(
                modifier = Modifier
                    .graphicsLayer(
                        translationX = offset.toFloat(),
                        alpha = iconsAlpha
                    ),
                onClick = onFavoriteClick
            ) {
                if (isFavorite) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = null,
                        tint = color
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                    )
                }
            }
        }
    )
}

@Composable
private fun PurchaseSection(
    price: Int,
    oldPrice: Int,
    quantity: Int,
    onRemoveItem: () -> Unit,
    onAddItem: () -> Unit,
    onAddToCart: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        if (oldPrice > price) {
            Text(
                text = stringResource(CoreR.string.dish_item_price, oldPrice),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                fontSize = 16.sp,
                textDecoration = TextDecoration.LineThrough
            )
            Text(
                text = stringResource(CoreR.string.dish_item_price, price),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )
        } else {
            Text(
                text = stringResource(CoreR.string.dish_item_price, price),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
            )
        }

        QuantityButton(
            color = MaterialTheme.colors.onSurface.copy(
                alpha = ButtonDefaults.OutlinedBorderOpacity
            ),
            quantity = quantity,
            onMinusClick = onRemoveItem,
            onPlusClick = onAddItem
        )
    }

    OutlinedButton(
        onClick = onAddToCart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(stringResource(CoreR.string.dish_button_add_to_cart))
    }
}

@Composable
private fun QuantityButton(
    quantity: Int,
    color: Color,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(36.dp)
            .border(
                width = 1.dp,
                color = color,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        val spacerModifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(color)

        val textModifier = Modifier
            .fillMaxHeight()
            .width(36.dp)
            .wrapContentHeight()

        Text(
            text = "-",
            modifier = Modifier
                .clickable(onClick = onMinusClick)
                .then(textModifier),
            textAlign = TextAlign.Center
        )
        Spacer(spacerModifier)
        Text(
            text = quantity.toString(),
            modifier = textModifier,
            textAlign = TextAlign.Center
        )
        Spacer(spacerModifier)
        Text(
            text = "+",
            modifier = Modifier
                .clickable(onClick = onPlusClick)
                .then(textModifier),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun DrawScope.pointGrid(
    startY: Float,
    lines: Int,
    verticalOffset: Float,
    horizontalOffset: Float,
    color: Color,
    width: Float,
    translationOffset: Int,
    movingLines: List<Pair<Int, Direction>>
) {
    repeat(lines) { index ->
        val offset = if (movingLines.fastMap { it.first }.contains(index)) {
            when (movingLines.first { it.first == index }.second) {
                Direction.LEFT -> translationOffset
                Direction.RIGHT -> -translationOffset
            }
        } else {
            0
        }
        pointLine(
            y = startY + verticalOffset * index,
            offset = horizontalOffset,
            color = color,
            width = width,
            translationOffset = offset
        )
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun DrawScope.pointLine(
    y: Float,
    offset: Float,
    width: Float,
    color: Color,
    translationOffset: Int
) {
    val offsets = buildList {
        repeat((width * 1.5 / offset).toInt()) { index ->
            add(
                Offset(
                    x = index * offset + translationOffset * 0.08f - 185,
                    y = y
                )
            )
        }
    }
    drawPoints(
        points = offsets,
        pointMode = PointMode.Points,
        color = color,
        strokeWidth = 20f,
        cap = StrokeCap.Round
    )
}

enum class Direction {
    LEFT,
    RIGHT
}