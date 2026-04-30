package com.trithuc.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.ltthuc.navigation.api.Navigator
import com.ltthuc.navigation.api.model.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
internal class NavigationViewModel @Inject constructor(
    navigator: Navigator,
) : ViewModel() {

    var navController: NavHostController? = null

    init {
        navigator
            .destination
            .onEach { destination ->
                val navController = navController ?: return@onEach
                navigateTo(navController, destination)
            }
            .launchIn(viewModelScope)
    }

    private fun navigateTo(
        navController: NavController,
        destination: Destination,
        builder: NavOptionsBuilder.() -> Unit = { },
    ) {
        when (destination) {
            Destination.Back -> navController.navigateUp()
            Destination.Main -> {
                navController.navigate(Destination.Main.route) {
                    val current = navController.currentBackStackEntry?.destination?.route
                    if (current == Destination.Splash.route) {
                        popUpTo(Destination.Splash.route) { inclusive = true }
                    }
                }
            }
            else -> navController.navigate(destination.route, builder)
        }
    }
}
