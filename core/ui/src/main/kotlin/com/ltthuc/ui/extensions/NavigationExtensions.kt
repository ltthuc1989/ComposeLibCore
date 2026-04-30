package com.ltthuc.ui.extensions

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Navigates only when the current destination is the [from] route. Prevents double-tap
 * crashes when the user spams a button while navigation is in flight.
 */
fun NavController.safeNavigate(
    route: String,
    from: String? = currentBackStackEntry?.destination?.route,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    if (currentBackStackEntry?.destination?.route == from) {
        navigate(route, builder)
    }
}
