package com.example.tableo.ui.guest.cart

/** Status beim Absenden der Bestellung. */
sealed interface SubmitState {
    data object Idle : SubmitState
    data object Loading : SubmitState
    data object Success : SubmitState
    data class Error(val message: String) : SubmitState
}
