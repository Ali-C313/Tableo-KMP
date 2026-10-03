package com.example.tableo

import androidx.compose.ui.window.ComposeUIViewController
import com.example.tableo.di.initKoin

// Koin darf nur einmal gestartet werden, auch wenn iOS den Controller neu baut
private var koinStarted = false

/** Einstieg für iOS, wird aus ContentView.swift aufgerufen. */
fun MainViewController() = ComposeUIViewController {
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }
    App()
}
