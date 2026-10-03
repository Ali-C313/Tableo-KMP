package com.example.tableo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.tableo.data.repository.ThemeRepository
import com.example.tableo.ui.AppStart
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.ThemeState
import org.koin.compose.koinInject

/** Einstieg für Android (MainActivity) und iOS (MainViewController). */
@Composable
fun App(themeRepository: ThemeRepository = koinInject()) {
    // gespeichertes Erscheinungsbild beim Start laden
    LaunchedEffect(Unit) {
        ThemeState.mode = themeRepository.getThemeMode()
    }

    TableoTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            AppStart()
        }
    }
}
