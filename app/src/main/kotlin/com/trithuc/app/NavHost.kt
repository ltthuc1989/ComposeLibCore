package com.trithuc.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ltthuc.navigation.api.model.Destination
import com.ltthuc.rating.api.RateHelper
import com.ltthuc.rating.ui.RateDialogHost
import com.ltthuc.utils.secrets.ISecretAdsKey

@Composable
fun NavHost(
    navController: NavHostController,
    adsKey: ISecretAdsKey,
    rateHelper: RateHelper,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Main.route,
    ) {
        composable(Destination.Main.route) { MainAppScreen(adsKey = adsKey, rateHelper = rateHelper) }
    }
    // Rate dialog overlay — observed across all destinations.
    RateDialogHost(rateHelper)
}
