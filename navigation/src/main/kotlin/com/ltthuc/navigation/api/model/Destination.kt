package com.ltthuc.navigation.api.model

import java.io.Serializable

sealed class Destination(val route: String = "") : Serializable {

    object Splash : Destination("splash")

    object Main : Destination("main")

    object Back : Destination()
}
