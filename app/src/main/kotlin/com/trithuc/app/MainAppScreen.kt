package com.trithuc.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ltthuc.ads.AdBanner
import com.ltthuc.ads.BannerType
import com.ltthuc.rating.api.RateHelper
import com.ltthuc.home.api.Home
import com.ltthuc.settings.api.Settings
import com.ltthuc.settings.R as SettingsR
import com.ltthuc.testcomponent.api.TestComponent
import com.ltthuc.ui.base.AppToolbarSmall
import com.ltthuc.ui.base.BaseScreen
import com.ltthuc.utils.secrets.ISecretAdsKey

@Composable
fun MainAppScreen(adsKey: ISecretAdsKey, rateHelper: RateHelper) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    // Rating count + threshold check are driven by MainActivity.onStart (lifecycle-aware,
    // fires on every Activity foreground transition regardless of recreate). Nothing rating-related here.

    BaseScreen(
        topBar = {
            AppToolbarSmall(
                title = when (selectedTab) {
                    0 -> "Yammy Delivery"
                    1 -> "Components"
                    else -> stringResource(SettingsR.string.settings_title)
                },
                centerTitle = true,
            )
        },
        bottomBar = {
            Column {
                AdBanner(
                    secret = adsKey,
                    bannerType = BannerType.ADAPTIVE_BANNER,
                    isAnchored = true,
                )
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home",
                            )
                        },
                        label = { Text("Home") },
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.Widgets else Icons.Outlined.Widgets,
                                contentDescription = "Components",
                            )
                        },
                        label = { Text("Components") },
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings",
                            )
                        },
                        label = { Text("Settings") },
                    )
                }
            }
        },
        handleNavigationBarPadding = false,
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "main-tab-content",
            ) { tab ->
                when (tab) {
                    0 -> Home(adsKey = adsKey)
                    1 -> TestComponent()
                    else -> Settings()
                }
            }
        }
    }
}

