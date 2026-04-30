package com.ltthuc.navigation.impl

import com.ltthuc.navigation.api.Navigator
import com.ltthuc.navigation.api.model.Destination
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow

@Singleton
internal class DefaultNavigator @Inject constructor() : Navigator {

    override val destination: MutableSharedFlow<Destination> = MutableSharedFlow(1)
    override val currentDestination: Destination?
        get() = destination.replayCache.firstOrNull()

    override fun back() {
        destination.tryEmit(Destination.Back)
    }

    override fun goTo(destination: Destination) {
        this.destination.tryEmit(destination)
    }
}