package com.example.tableo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.tableo.ui.AppStart
import com.example.tableo.ui.AppViewModel
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.util.AppLocaleEnvironment
import org.koin.compose.koinInject

/** Einstieg für Android (MainActivity) und iOS (MainViewController). */
@Composable
fun App(appViewModel: AppViewModel = koinInject()) {
    // gespeichertes Erscheinungsbild und gespeicherte Sprache beim Start laden
    LaunchedEffect(Unit) { appViewModel.loadSettings() }

    AppLocaleEnvironment {
        TableoTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) {
                AppStart()
            }
        }
    }
}
