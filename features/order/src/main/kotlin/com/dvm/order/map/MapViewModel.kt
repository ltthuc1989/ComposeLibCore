package com.dvm.order.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.dvm.navigation.api.Navigator
import com.dvm.navigation.api.model.Destination
import com.dvm.order.map.model.MapState
import com.dvm.utils.getErrorMessage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.dvm.ui.R as CoreR

@HiltViewModel
internal class MapViewModel @Inject constructor(
    private val navigator: Navigator,
    savedState: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(MapState())
        private set

    private val addressItems = savedState.getLiveData("address_items", emptyList<String>())

    init {
        addressItems
            .asFlow()
            .distinctUntilChanged()
            .map { it.joinToString(", ") }
            .onEach { state = state.copy(address = it) }
            .launchIn(viewModelScope)
    }

    fun dismissAlert() {
        state = state.copy(alert = null)
    }

    fun onMapReady(
        context: Context,
        map: GoogleMap
    ) {
        moveToLocation(context, map)

        map
            .locationFlow()
            .distinctUntilChanged()
            .debounce(500)
            .catch { throwable ->
                state = state.copy(alert = throwable.getErrorMessage())
            }
            .onEach { latLng ->
                addressItems.value = getAddress(
                    context = context,
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                )
            }
            .launchIn(viewModelScope)
    }

    fun onLocationPermissionGranted(
        context: Context,
        map: GoogleMap
    ) {
        moveToLocation(context, map)
    }

    fun onButtonCompleteClick() {
        if (addressItems.value?.size == 3) {
            navigator.goTo(
                Destination.BackToOrdering(
                    addressItems.value.orEmpty().joinToString(", ")
                )
            )
        } else {
            state = state.copy(alert = CoreR.string.ordering_address_error)
        }
    }

    @SuppressLint("MissingPermission")
    private fun moveToLocation(
        context: Context,
        map: GoogleMap,
        defaultLat: Double = 55.752,
        defaultLng: Double = 37.624,
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val move = { lat: Double, lng: Double ->
                val location = LatLng(lat, lng)
                val defaultMarker =
                    MarkerOptions()
                        .draggable(true)
                        .position(location)
                map.addMarker(defaultMarker)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
            }

            LocationServices
                .getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { location ->
                    val (latitude, longitude) = if (location != null) {
                        location.latitude to location.longitude
                    } else {
                        defaultLat to defaultLng
                    }
                    move(latitude, longitude)
                    addressItems.value = getAddress(
                        context = context,
                        latitude = latitude,
                        longitude = longitude
                    )
                }
                .addOnFailureListener {
                    move(defaultLat, defaultLng)
                    addressItems.value = getAddress(
                        context = context,
                        latitude = defaultLat,
                        longitude = defaultLng
                    )
                }
        }
    }

    private fun getAddress(
        context: Context,
        latitude: Double,
        longitude: Double
    ): List<String> {
        val locationAddress = try {
            Geocoder(context)
                .getFromLocation(latitude, longitude, 1)
                ?.firstOrNull()
                ?: return emptyList()
        } catch (exception: CancellationException) {
            throw CancellationException()
        } catch (exception: Exception) {
            state = state.copy(alert = CoreR.string.message_general_error)
            return emptyList()
        }
        val city = locationAddress.subAdminArea?.let {
            context.getString(CoreR.string.ordering_address_city, locationAddress.subAdminArea)
        }
        val building = locationAddress.thoroughfare?.let {
            context.getString(CoreR.string.ordering_address_street, locationAddress.thoroughfare)
        }
        val flat = locationAddress.subThoroughfare?.let {
            context.getString(CoreR.string.ordering_address_flat, locationAddress.subThoroughfare)
        }
        return listOfNotNull(city, building, flat)
    }

    private fun GoogleMap.locationFlow() = callbackFlow<LatLng> {
        setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {
                trySend(marker.position)
            }

            override fun onMarkerDragStart(marker: Marker) {
                /* do nothing */
            }

            override fun onMarkerDragEnd(marker: Marker) {
                /* do nothing */
            }
        })
        awaitClose { setOnMarkerDragListener(null) }
    }
}