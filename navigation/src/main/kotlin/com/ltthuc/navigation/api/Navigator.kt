package com.ltthuc.navigation.api

import com.ltthuc.navigation.api.model.Destination
import kotlinx.coroutines.flow.SharedFlow

interface Navigator {

    val destination: SharedFlow<Destination>
    val currentDestination: Destination?

    fun back()
    fun goTo(destination: Destination)
}