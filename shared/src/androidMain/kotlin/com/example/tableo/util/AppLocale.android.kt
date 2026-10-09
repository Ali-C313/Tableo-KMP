package com.example.tableo.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual object LocalAppLocale {
    // Sprache vom Handy, bevor die App sie überschreibt
    private var systemDefault: Locale? = null

    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        if (systemDefault == null) systemDefault = Locale.getDefault()

        val newLocale = if (value == null) systemDefault!! else Locale(value)
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        val resources = LocalContext.current.resources
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return LocalConfiguration.provides(configuration)
    }
}
