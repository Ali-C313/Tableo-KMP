package com.example.tableo.ui.guest.menu

/** Zustand beim Laden der Wechselkurse. */
sealed interface ExchangeRateUiState {
    data object Idle : ExchangeRateUiState
    data object Loading : ExchangeRateUiState
    data class Success(val rates: Map<String, Double>) : ExchangeRateUiState
    data class Error(val message: String) : ExchangeRateUiState
}
