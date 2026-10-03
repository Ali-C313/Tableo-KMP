package com.example.tableo.data.repository.impl

import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.DeliveryAddress
import com.example.tableo.data.repository.LocationRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.CLPlacemark
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Standort über CoreLocation, Adressen über den CLGeocoder von iOS. */
class LocationRepositoryImpl : LocationRepository {

    // iOS hält Manager und Delegate nur schwach fest, deshalb hier merken solange die Abfrage läuft
    private var singleRequestManager: CLLocationManager? = null
    private var singleRequestDelegate: LocationDelegate? = null

    override suspend fun getCurrentAddress(): Result<DeliveryAddress> = runCatching {
        val location = currentLocation()
        val coordinates = location.toCoordinates()
        DeliveryAddress(text = addressText(location, coordinates), coordinates = coordinates)
    }

    override suspend fun searchAddress(query: String): Result<DeliveryAddress> = runCatching {
        val placemark = suspendCancellableCoroutine<CLPlacemark> { continuation ->
            CLGeocoder().geocodeAddressString(query) { placemarks, error ->
                val found = placemarks?.firstOrNull() as? CLPlacemark
                when {
                    found != null -> continuation.resume(found)
                    else -> continuation.resumeWithException(
                        IllegalStateException(error?.localizedDescription ?: "Adresse nicht gefunden")
                    )
                }
            }
        }
        val location = placemark.location ?: error("Adresse nicht gefunden")
        DeliveryAddress(
            text = format(placemark).ifBlank { query },
            coordinates = location.toCoordinates()
        )
    }

    // intervalMillis gibt es auf iOS so nicht, dafür meldet iOS alle 10 Meter Bewegung
    override fun locationUpdates(intervalMillis: Long): Flow<Coordinates> = callbackFlow {
        val manager = CLLocationManager()
        val delegate = LocationDelegate(
            onLocation = { location -> trySend(location.toCoordinates()) },
            onError = { message -> close(IllegalStateException(message)) }
        )
        manager.delegate = delegate
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.distanceFilter = 10.0
        manager.startUpdatingLocation()

        // Updates stoppen, sobald niemand mehr zuhört. Der Delegate wird hier mit festgehalten
        awaitClose {
            manager.stopUpdatingLocation()
            manager.delegate = null
            delegate.hashCode()
        }
    }

    /** Einmal den aktuellen Standort holen. */
    private suspend fun currentLocation(): CLLocation = suspendCancellableCoroutine { continuation ->
        val manager = CLLocationManager()
        val delegate = LocationDelegate(
            onLocation = { location ->
                manager.stopUpdatingLocation()
                if (continuation.isActive) continuation.resume(location)
            },
            onError = { message ->
                if (continuation.isActive) continuation.resumeWithException(IllegalStateException(message))
            }
        )
        singleRequestManager = manager
        singleRequestDelegate = delegate
        manager.delegate = delegate
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.requestLocation()
        continuation.invokeOnCancellation { manager.stopUpdatingLocation() }
    }

    /** Macht aus dem Standort eine Adresse. Klappt das nicht, stehen die Koordinaten selbst da. */
    private suspend fun addressText(location: CLLocation, coordinates: Coordinates): String {
        val placemark = suspendCancellableCoroutine<CLPlacemark?> { continuation ->
            CLGeocoder().reverseGeocodeLocation(location) { placemarks, _ ->
                continuation.resume(placemarks?.firstOrNull() as? CLPlacemark)
            }
        }
        return placemark?.let { format(it) }?.takeIf { it.isNotBlank() }
            ?: "${coordinates.latitude}, ${coordinates.longitude}"
    }

    /** "Straße Nr, PLZ Ort" */
    private fun format(placemark: CLPlacemark): String {
        val street = listOfNotNull(placemark.thoroughfare, placemark.subThoroughfare).joinToString(" ")
        val city = listOfNotNull(placemark.postalCode, placemark.locality).joinToString(" ")
        return listOf(street, city).filter { it.isNotBlank() }.joinToString(", ")
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun CLLocation.toCoordinates(): Coordinates =
    coordinate.useContents { Coordinates(latitude, longitude) }

/** Leitet die Antworten vom CLLocationManager an Kotlin-Lambdas weiter. */
private class LocationDelegate(
    private val onLocation: (CLLocation) -> Unit,
    private val onError: (String) -> Unit
) : NSObject(), CLLocationManagerDelegateProtocol {

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        (didUpdateLocations.lastOrNull() as? CLLocation)?.let(onLocation)
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        onError(didFailWithError.localizedDescription)
    }
}
