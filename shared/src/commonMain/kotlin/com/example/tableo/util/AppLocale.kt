package com.example.tableo.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.tableo.data.enums.AppLanguage

/** Gewählte Sprache der App, änderbar aus den Einstellungen. */
object LanguageState {
    var language by mutableStateOf(AppLanguage.SYSTEM)
}

/** Setzt die Sprache für die Compose-Ressourcen, auf Android und iOS unterschiedlich. */
expect object LocalAppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}

/**
 * Hüllt die ganze App ein: gewählte Sprache für stringResource und bei Arabisch
 * das Layout von rechts nach links. Beim Sprachwechsel wird alles neu gezeichnet.
 */
@Composable
fun AppLocaleEnvironment(content: @Composable () -> Unit) {
    val language = LanguageState.language
    CompositionLocalProvider(
        LocalAppLocale provides language.code,
        LocalLayoutDirection provides if (language.isRightToLeft) LayoutDirection.Rtl else LocalLayoutDirection.current
    ) {
        key(language) {
            content()
        }
    }
}
