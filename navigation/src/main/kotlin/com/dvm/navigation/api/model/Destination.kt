package com.dvm.navigation.api.model

import java.io.Serializable

sealed class Destination(val route: String = "") : Serializable {

    object Splash : Destination("splash")

    object Main : Destination("main")

    object Menu : Destination("menu")

    object Search : Destination("search")

    object Favorite : Destination("favorite")

    object Orders : Destination("orders")

    object Ordering : Destination("ordering")

    object Registration : Destination("registration")

    object FinishRegister : Destination()

    object PasswordRestoration : Destination("restoration")

    object Profile : Destination("profile")

    object Cart : Destination("cart")

    object Notification : Destination("notification")

    object Back : Destination()

    object LoginTarget : Destination()

    data class BackToOrdering(val address: String) : Destination()

    class Map : Destination(ROUTE) {
        companion object {
            const val ROUTE = "map"
            const val MAP_ADDRESS = "map_back_address_result"
        }
    }

    data class Login(
        val targetDestination: Destination? = null
    ) : Destination(ROUTE) {
        companion object {
            const val ROUTE = "login"
        }
    }

    data class Dish(
        val dishId: String
    ) : Destination(ROUTE) {
        fun createRoute(dishId: String) = "dish/$dishId"

        companion object {
            const val ROUTE = "dish/{dishId}"
            const val DISH_ID = "dishId"
        }
    }

    data class Order(
        val orderId: String
    ) : Destination(ROUTE) {
        fun createRoute(orderId: String) = "order/$orderId"

        companion object {
            const val ROUTE = "order/{orderId}"
            const val ORDER_ID = "orderId"
        }
    }

    data class Category(
        val categoryId: String,
        val subcategoryId: String? = null
    ) : Destination(ROUTE) {

        fun createRoute(
            categoryId: String,
            subcategoryId: String?
        ) =
            "category/$categoryId/?subcategoryId=$subcategoryId"

        companion object {
            const val ROUTE = "category/{categoryId}/?subcategoryId={subcategoryId}"
            const val CATEGORY_ID = "categoryId"
            const val SUBCATEGORY_ID = "subcategoryId"
        }
    }
}
