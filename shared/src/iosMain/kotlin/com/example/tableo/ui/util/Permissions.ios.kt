package com.example.tableo.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalInspectionMode
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.NSObject

@Composable
actual fun rememberLocationPermissionRequest(onResult: (Boolean) -> Unit): () -> Unit {
    if (LocalInspectionMode.current) return {}

    val currentOnResult by rememberUpdatedState(onResult)
    val helper = remember { LocationPermissionHelper() }
    return remember(helper) { { helper.request { granted -> currentOnResult(granted) } } }
}

@Composable
actual fun RequestNotificationPermission() {
    if (LocalInspectionMode.current) return

    LaunchedEffect(Unit) {
        UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { _, _ -> }
    }
}

/**
 * Fragt beim CLLocationManager nach "Beim Verwenden der App". Ist schon entschieden,
 * kommt die Antwort sofort, sonst erst wenn der Nutzer im Dialog gewählt hat.
 */
private class LocationPermissionHelper : NSObject(), CLLocationManagerDelegateProtocol {

    private val manager = CLLocationManager()
    private var callback: ((Boolean) -> Unit)? = null

    init {
        manager.delegate = this
    }

    fun request(onResult: (Boolean) -> Unit) {
        when (manager.authorizationStatus) {
            kCLAuthorizationStatusNotDetermined -> {
                callback = onResult
                manager.requestWhenInUseAuthorization()
            }
            kCLAuthorizationStatusAuthorizedWhenInUse, kCLAuthorizationStatusAuthorizedAlways -> onResult(true)
            else -> onResult(false)
        }
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus
        if (status == kCLAuthorizationStatusNotDetermined) return
        callback?.invoke(status == kCLAuthorizationStatusAuthorizedWhenInUse || status == kCLAuthorizationStatusAuthorizedAlways)
        callback = null
    }
}
