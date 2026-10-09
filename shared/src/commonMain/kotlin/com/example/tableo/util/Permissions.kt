package com.example.tableo.util

import androidx.compose.runtime.Composable

/**
 * Fragt die Standort-Berechtigung ab. Ist sie schon erteilt, kommt sofort true zurück.
 * Android nutzt den Activity-Result-Launcher, iOS den CLLocationManager.
 *
 * @param onResult true, wenn der Standort erlaubt wurde
 * @return Funktion, die die Abfrage startet
 */
@Composable
expect fun rememberLocationPermissionRequest(onResult: (Boolean) -> Unit): () -> Unit

/** Fragt einmal nach der Erlaubnis für Benachrichtigungen. In der Preview passiert nichts. */
@Composable
expect fun RequestNotificationPermission()
