package com.trithuc.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ltthuc.navigation.api.model.Destination
import com.ltthuc.splash.api.Splash
import com.ltthuc.utils.secrets.ISecretAdsKey

@Composable
fun NavHost(
    navController: NavHostController,
    startDestination: String,
    adsKey: ISecretAdsKey,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Destination.Splash.route) { Splash() }
        composable(Destination.Main.route) { MainAppScreen(adsKey = adsKey) }
    }
}
