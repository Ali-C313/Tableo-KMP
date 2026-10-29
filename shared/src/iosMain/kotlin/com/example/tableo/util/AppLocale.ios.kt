package com.example.tableo.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

/** iOS: Sprache über "AppleLanguages" in den UserDefaults. */
actual object LocalAppLocale {
    private const val LANGUAGES_KEY = "AppleLanguages"

    // Sprache vom iPhone, bevor die App sie überschreibt
    private val systemDefault = NSLocale.preferredLanguages.first() as String
    private val localAppLocale = staticCompositionLocalOf { systemDefault }

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val newLocale = value ?: systemDefault
        if (value == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANGUAGES_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(listOf(newLocale), LANGUAGES_KEY)
        }
        return localAppLocale.provides(newLocale)
    }
}
