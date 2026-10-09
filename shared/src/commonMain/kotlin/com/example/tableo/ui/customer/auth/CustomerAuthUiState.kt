package com.example.tableo.ui.customer.auth

import com.example.tableo.data.model.StaffAccess

/** Zustand bei Login/Registrierung. */
sealed interface CustomerAuthUiState {
    data object Idle : CustomerAuthUiState
    data object Loading : CustomerAuthUiState
    data object ResetEmailSent : CustomerAuthUiState
    /** Konto war zum Löschen vorgemerkt und ist durch die Anmeldung wieder aktiv. */
    data class Restored(val access: StaffAccess?) : CustomerAuthUiState
    data class Error(val message: String) : CustomerAuthUiState
}
