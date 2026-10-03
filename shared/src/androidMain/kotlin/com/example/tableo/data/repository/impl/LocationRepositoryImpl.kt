package com.example.tableo.data.repository.impl

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.DeliveryAddress
import com.example.tableo.data.repository.LocationRepository
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

/** Standort über den FusedLocationProvider, Adressen über den Geocoder von Android. */
class LocationRepositoryImpl(
    context: Context
) : LocationRepository {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context, Locale.GERMANY)

    // Berechtigung wird vorher im Screen abgefragt, fehlt sie trotzdem, fängt runCatching die SecurityException
    override suspend fun getCurrentAddress(): Result<DeliveryAddress> = runCatching {
        val location = fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            ?: error("Standort konnte nicht ermittelt werden")
        val coordinates = Coordinates(location.latitude, location.longitude)
        DeliveryAddress(text = addressText(coordinates), coordinates = coordinates)
    }

    @Suppress("DEPRECATION")
    override suspend fun searchAddress(query: String): Result<DeliveryAddress> = runCatching {
        withContext(Dispatchers.IO) {
            val address = geocoder.getFromLocationName(query, 1)?.firstOrNull()
                ?: error("Adresse nicht gefunden")
            DeliveryAddress(
                text = format(address).ifBlank { query },
                coordinates = Coordinates(address.latitude, address.longitude)
            )
        }
    }
    override fun locationUpdates(intervalMillis: Long): Flow<Coordinates> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(Coordinates(it.latitude, it.longitude)) }
            }
        }
        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        // Updates stoppen, sobald niemand mehr zuhört
        awaitClose { fusedClient.removeLocationUpdates(callback) }
    }

    /** Macht aus Koordinaten eine Adresse. Klappt das nicht, stehen die Koordinaten selbst da. */
    @Suppress("DEPRECATION")
    private suspend fun addressText(coordinates: Coordinates): String = withContext(Dispatchers.IO) {
        val address = runCatching {
            geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)?.firstOrNull()
        }.getOrNull()
        address?.let { format(it) }?.takeIf { it.isNotBlank() }
            ?: String.format(Locale.GERMANY, "%.5f, %.5f", coordinates.latitude, coordinates.longitude)
    }

    /** "Straße Nr, PLZ Ort" */
    private fun format(address: Address): String {
        val street = listOfNotNull(address.thoroughfare, address.subThoroughfare).joinToString(" ")
        val city = listOfNotNull(address.postalCode, address.locality).joinToString(" ")
        return listOf(street, city).filter { it.isNotBlank() }.joinToString(", ")
            .ifBlank { address.getAddressLine(0).orEmpty() }
    }
}
