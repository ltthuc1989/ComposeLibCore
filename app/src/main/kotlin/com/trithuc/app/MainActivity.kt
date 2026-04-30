package com.trithuc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.ltthuc.navigation.api.model.Destination
import com.ltthuc.ui.themes.ComposeTemplateTheme
import com.ltthuc.utils.secrets.ISecretAdsKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class MainActivity : ComponentActivity() {

    @Inject lateinit var adsKey: ISecretAdsKey

    private val navigationViewModel: NavigationViewModel by viewModels()
    private val sharedViewModel: AppSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by sharedViewModel.themeMode.collectAsState()
            ComposeTemplateTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                navigationViewModel.navController = navController
                NavHost(
                    navController = navController,
                    startDestination = Destination.Splash.route,
                    adsKey = adsKey,
                )
            }
        }
    }
}
