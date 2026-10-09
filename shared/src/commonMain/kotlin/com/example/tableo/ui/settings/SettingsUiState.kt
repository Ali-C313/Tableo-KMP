package com.example.tableo.ui.settings

/** Zustand beim Speichern von Name, E-Mail oder Passwort. */
sealed interface SettingsUiState {
    data object Idle : SettingsUiState
    data object Saving : SettingsUiState
    data object Saved : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}
