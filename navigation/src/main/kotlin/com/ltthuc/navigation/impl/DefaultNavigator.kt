package com.ltthuc.navigation.impl

import com.ltthuc.navigation.api.Navigator
import com.ltthuc.navigation.api.model.Destination
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow

@Singleton
internal class DefaultNavigator @Inject constructor() : Navigator {

    // replay=0 to avoid re-emitting the last navigation when a NEW collector subscribes
    // (e.g. after Activity recreate). Otherwise the cached `Destination.Main` replays and races
    // with Splash's own emit, producing duplicate Main entries in the backstack so BACK
    // doesn't finish the Activity. extraBufferCapacity=1 keeps `tryEmit` non-blocking.
    override val destination: MutableSharedFlow<Destination> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
    )
    override val currentDestination: Destination?
        get() = destination.replayCache.firstOrNull()

    override fun back() {
        destination.tryEmit(Destination.Back)
    }

    override fun goTo(destination: Destination) {
        this.destination.tryEmit(destination)
    }
}