package com.example.tableo.ui.components

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tableo.data.model.Coordinates
import com.example.tableo.resources.Res
import com.example.tableo.resources.address_title
import com.example.tableo.resources.map_unavailable
import com.example.tableo.resources.tracking_driver_fallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import org.jetbrains.compose.resources.stringResource

/** Google-Karte mit Fahrer (orange) und Lieferadresse (blau). */
@Composable
actual fun TrackingMap(
    driver: Coordinates,
    destination: Coordinates?,
    modifier: Modifier
) {
    // in der Preview gibt es keine echte Karte
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) { Text("Karte") }
        return
    }

    // ohne API-Key bleibt die Google-Karte nur grau, dann lieber einen Hinweis zeigen.
    // Den Key trägt man in local.properties als MAPS_API_KEY ein
    val context = LocalContext.current
    val hasMapsKey = remember {
        runCatching {
            @Suppress("DEPRECATION")
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            !appInfo.metaData?.getString("com.google.android.geo.API_KEY").isNullOrBlank()
        }.getOrDefault(false)
    }
    if (!hasMapsKey) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(Res.string.map_unavailable),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val addressTitle = stringResource(Res.string.address_title)
    val driverTitle = stringResource(Res.string.tracking_driver_fallback)

    val driverLatLng = LatLng(driver.latitude, driver.longitude)
    val destinationLatLng = destination?.let { LatLng(it.latitude, it.longitude) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverLatLng, 14f)
    }
    val driverMarker = rememberMarkerState(position = driverLatLng)
    val destinationMarker = rememberMarkerState(position = destinationLatLng ?: driverLatLng)

    // bei jedem neuen Standort Marker verschieben und Kamera so setzen, dass beide zu sehen sind
    LaunchedEffect(driverLatLng, destinationLatLng) {
        driverMarker.position = driverLatLng
        destinationLatLng?.let { destinationMarker.position = it }

        val update = if (destinationLatLng != null) {
            val bounds = LatLngBounds.builder().include(driverLatLng).include(destinationLatLng).build()
            CameraUpdateFactory.newLatLngBounds(bounds, 150)
        } else {
            CameraUpdateFactory.newLatLngZoom(driverLatLng, 15f)
        }
        // klappt nicht, solange die Karte noch keine Größe hat, dann einfach auf den Fahrer
        runCatching { cameraPositionState.animate(update) }
            .onFailure { cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(driverLatLng, 14f)) }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        if (destinationLatLng != null) {
            Marker(
                state = destinationMarker,
                title = addressTitle,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
        Marker(
            state = driverMarker,
            title = driverTitle,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        )
    }
}
