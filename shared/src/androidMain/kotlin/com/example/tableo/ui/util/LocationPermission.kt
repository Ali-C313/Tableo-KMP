package com.example.tableo.ui.util

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

private val LocationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

/**
 * Fragt die Standort-Berechtigung ab. Ist sie schon erteilt, kommt sofort true zurück.
 * In der Preview gibt es keinen Launcher, dann passiert beim Aufruf einfach nichts.
 *
 * @param onResult true, wenn genauer oder ungefährer Standort erlaubt wurde
 * @return Funktion, die die Abfrage startet
 */
@Composable
actual fun rememberLocationPermissionRequest(onResult: (Boolean) -> Unit): () -> Unit {
    if (LocalInspectionMode.current) return {}

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result -> onResult(result.values.any { it }) }

    return { launcher.launch(LocationPermissions) }
}
