package com.example.tableo.ui.staff.menu

import com.example.tableo.data.model.MenuItem

/** Zustand der Speisekarten-Verwaltung fürs Personal. */
sealed interface StaffMenuUiState {
    data object Loading : StaffMenuUiState
    data class Success(val items: List<MenuItem>) : StaffMenuUiState
    data class Error(val message: String) : StaffMenuUiState
}
