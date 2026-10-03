package com.example.tableo.ui.util

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalInspectionMode

/**
 * Fragt ab Android 13 einmal nach der Erlaubnis für Benachrichtigungen.
 * Darunter braucht es keine Abfrage, in der Preview passiert nichts.
 */
@Composable
actual fun RequestNotificationPermission() {
    if (LocalInspectionMode.current || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
