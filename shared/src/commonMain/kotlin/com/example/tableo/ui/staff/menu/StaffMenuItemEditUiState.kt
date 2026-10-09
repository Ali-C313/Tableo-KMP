package com.example.tableo.ui.staff.menu

/** Zustand beim Speichern eines Speisekarten-Artikels. */
sealed interface StaffMenuItemEditUiState {
    data object Idle : StaffMenuItemEditUiState
    data object Saving : StaffMenuItemEditUiState
    data object Saved : StaffMenuItemEditUiState
    data class Error(val message: String) : StaffMenuItemEditUiState
}
