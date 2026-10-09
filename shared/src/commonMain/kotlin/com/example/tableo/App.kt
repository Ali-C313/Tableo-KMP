package com.example.tableo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.tableo.data.repository.LanguageRepository
import com.example.tableo.data.repository.ThemeRepository
import com.example.tableo.ui.AppStart
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.ThemeState
import com.example.tableo.util.AppLocaleEnvironment
import com.example.tableo.util.LanguageState
import org.koin.compose.koinInject

/** Einstieg für Android (MainActivity) und iOS (MainViewController). */
@Composable
fun App(
    themeRepository: ThemeRepository = koinInject(),
    languageRepository: LanguageRepository = koinInject()
) {
    // gespeichertes Erscheinungsbild und gespeicherte Sprache beim Start laden
    LaunchedEffect(Unit) {
        ThemeState.mode = themeRepository.getThemeMode()
        LanguageState.language = languageRepository.getLanguage()
    }

    AppLocaleEnvironment {
        TableoTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) {
                AppStart()
            }
        }
    }
}
