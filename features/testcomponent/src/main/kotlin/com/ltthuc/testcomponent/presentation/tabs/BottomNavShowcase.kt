package com.ltthuc.testcomponent.presentation.tabs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ltthuc.ui.themes.Dimens

private data class BottomNavItem(
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
    val badgeCount: Int = 0,
)

@Composable
fun BottomNavShowcase(modifier: Modifier = Modifier) {
    val items = remember {
        listOf(
            BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
            BottomNavItem("Search", Icons.Filled.Search, Icons.Outlined.Search),
            BottomNavItem("Favorites", Icons.Filled.Favorite, Icons.Outlined.Favorite, badgeCount = 3),
            BottomNavItem("Alerts", Icons.Filled.Notifications, Icons.Outlined.Notifications, badgeCount = 12),
            BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person),
        )
    }
    var selected by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        SectionLabel(text = "Material 3 NavigationBar")

        // Preview the live nav bar tied to the demo content below.
        Surface(
            shape = RoundedCornerShape(Dimens.radiusM),
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingM),
        ) {
            Column {
                ContentArea(
                    label = items[selected].label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                )
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selected == index,
                            onClick = { selected = index },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item.badgeCount > 0) {
                                            Badge {
                                                Text(if (item.badgeCount > 99) "99+" else item.badgeCount.toString())
                                            }
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = if (selected == index) item.iconSelected else item.iconUnselected,
                                        contentDescription = item.label,
                                    )
                                }
                            },
                            label = { Text(item.label) },
                            alwaysShowLabel = false,
                        )
                    }
                }
            }
        }

        SectionLabel(text = "Static — labels always shown")
        Surface(
            shape = RoundedCornerShape(Dimens.radiusM),
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingM),
        ) {
            NavigationBar {
                items.take(3).forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == 0,
                        onClick = {},
                        icon = { Icon(item.iconUnselected, contentDescription = item.label) },
                        label = { Text(item.label) },
                        alwaysShowLabel = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentArea(label: String, modifier: Modifier) {
    AnimatedContent(
        targetState = label,
        transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
        modifier = modifier,
        label = "bottom-nav-content",
    ) { value ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = Dimens.radiusM, topEnd = Dimens.radiusM))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(Dimens.spacingL),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingS),
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Tap a tab below to switch.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun tween(duration: Int) = androidx.compose.animation.core.tween<Float>(durationMillis = duration)
