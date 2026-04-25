package com.dvm.order.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvm.order.map.model.MapState
import com.dvm.ui.components.Alert
import com.dvm.ui.components.AlertButton
import androidx.compose.foundation.layout.statusBarsPadding
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.launch
import com.dvm.ui.R as CoreR

@Composable
internal fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val state: MapState = viewModel.state

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val map = rememberMapViewWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                val googleMap = map.awaitMap()
                viewModel.onLocationPermissionGranted(context, googleMap)
            }
        }
    }

    LaunchedEffect(map) {
        val googleMap = map.awaitMap()
        viewModel.onMapReady(context, googleMap)
        val locationPermission =
            ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(ACCESS_FINE_LOCATION)
        }
    }

    AndroidView({ map })

    Box(Modifier.fillMaxSize()) {
        Text(
            text = state.address,
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp)
                .background(
                    color = Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    color = Color.Black.copy(alpha = 0.8f),
                    width = 1.dp,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(10.dp)
        )

        Button(
            onClick = { viewModel.onButtonCompleteClick() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp)
                .padding(bottom = 60.dp)
                .fillMaxWidth()
        ) {
            Text(stringResource(CoreR.string.ordering_map_button_complete))
        }
    }

    if (state.alert != null) {
        val onDismiss = { viewModel.dismissAlert() }
        Alert(
            message = stringResource(state.alert),
            onDismiss = onDismiss,
            buttons = { AlertButton(onClick = onDismiss) }
        )
    }
}