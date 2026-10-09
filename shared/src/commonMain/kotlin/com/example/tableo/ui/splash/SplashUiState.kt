package com.example.tableo.ui.splash

/** Zustand beim Start: Loading bis geklärt ist, ob das Onboarding schon gezeigt wurde. */
sealed interface SplashUiState {
    data object Loading : SplashUiState
    data class Ready(val showOnboarding: Boolean) : SplashUiState
}
