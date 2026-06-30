package com.trithuc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.ltthuc.rating.api.RateHelper
import com.ltthuc.ui.themes.ComposeTemplateTheme
import com.ltthuc.utils.secrets.ISecretAdsKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class MainActivity : ComponentActivity() {

    @Inject lateinit var adsKey: ISecretAdsKey
    @Inject lateinit var rateHelper: RateHelper

    private val navigationViewModel: NavigationViewModel by viewModels()
    private val sharedViewModel: AppSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplash = true
        installSplashScreen().setKeepOnScreenCondition { keepSplash }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(SPLASH_MIN_DURATION_MS)
            keepSplash = false
        }
        setContent {
            val themeMode by sharedViewModel.themeMode.collectAsState()
            ComposeTemplateTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                navigationViewModel.navController = navController
                NavHost(
                    navController = navController,
                    adsKey = adsKey,
                    rateHelper = rateHelper,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Fires every time MainActivity becomes STARTED — cold start, back+reopen,
        // home+reopen, switch-app+resume. Single source of truth for "app open" counter.
        // Activity-level lifecycle works reliably even when Compose Nav backstack quirk
        // keeps Activity alive on BACK (NavHost consumes BACK at start dest, no recreate).
        lifecycleScope.launch {
            rateHelper.increaseCount()
            rateHelper.requestRate(this@MainActivity)
        }
    }

    private companion object {
        const val SPLASH_MIN_DURATION_MS = 800L
    }
}
