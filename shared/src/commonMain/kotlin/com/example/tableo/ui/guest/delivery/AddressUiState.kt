package com.example.tableo.ui.guest.delivery

/** Zustand beim Suchen der Adresse. */
sealed interface AddressUiState {
    data object Idle : AddressUiState
    data object Loading : AddressUiState
    data object Success : AddressUiState
    data class Error(val message: String) : AddressUiState
}
