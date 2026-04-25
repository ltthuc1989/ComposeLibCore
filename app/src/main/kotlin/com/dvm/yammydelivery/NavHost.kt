package com.dvm.yammydelivery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dvm.auth.api.Login
import com.dvm.auth.api.PasswordRestoration
import com.dvm.auth.api.Registration
import com.dvm.cart.api.Cart
import com.dvm.dish.api.Dish
import com.dvm.menu.api.Category
import com.dvm.menu.api.Favorite
import com.dvm.menu.api.Main
import com.dvm.menu.api.Menu
import com.dvm.menu.api.Search
import com.dvm.navigation.api.model.Destination
import com.dvm.notifications.api.Notification
import com.dvm.order.api.Map
import com.dvm.order.api.Order
import com.dvm.order.api.Ordering
import com.dvm.order.api.Orders
import com.dvm.profile.api.Profile
import com.dvm.splash.api.Splash
import com.dvm.utils.BackStackValueObserver
import com.dvm.utils.addUri
import com.dvm.utils.navDeepLinks

@Composable
fun NavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.Splash.route) { Splash() }
        composable(Destination.Main.route) { Main() }
        composable(Destination.Menu.route) { Menu() }
        composable(Destination.Search.route) { Search() }
        composable(Destination.Favorite.route) { Favorite() }
        composable(Destination.Login.ROUTE) { Login() }
        composable(Destination.Registration.route) { Registration() }
        composable(Destination.Cart.route) { Cart() }
        composable(Destination.Orders.route) { Orders() }
        composable(Destination.Profile.route) { Profile() }
        composable(Destination.Map.ROUTE) { Map() }
        composable(Destination.Dish.ROUTE) { Dish() }
        composable(Destination.Order.ROUTE) { Order() }
        composable(Destination.PasswordRestoration.route) { PasswordRestoration() }

        composable(
            route = Destination.Notification.route,
            deepLinks = navDeepLinks.addUri(NOTIFICATION_URI)
        ) {
            Notification()
        }

        composable(Destination.Ordering.route) {
            var address by remember { mutableStateOf("") }
            navController.BackStackValueObserver<String>(Destination.Map.MAP_ADDRESS) {
                address = it
            }
            Ordering(address)
        }

        composable(
            route = Destination.Category.ROUTE,
            arguments = listOf(
                navArgument(Destination.Category.SUBCATEGORY_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            Category()
        }
    }
}

internal const val NOTIFICATION_URI = "app://com.dvm.yammydelivery"