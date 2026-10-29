package com.example.tableo.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/** Android: Sprache über Locale und die Konfiguration der Ressourcen. */
actual object LocalAppLocale {
    // Sprache vom Handy, bevor die App sie überschreibt
    private var systemDefault: Locale? = null

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        val deviceLocale = systemDefault ?: Locale.getDefault().also { systemDefault = it }

        val newLocale = if (value == null) deviceLocale else Locale.forLanguageTag(value)
        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        val resources = LocalContext.current.resources
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return LocalConfiguration.provides(configuration)
    }
}
