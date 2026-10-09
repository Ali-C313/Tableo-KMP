package com.example.tableo.ui.staff.archive

import com.example.tableo.data.model.Order

/** Zustand des Archivs. */
sealed interface StaffArchiveUiState {
    data object Loading : StaffArchiveUiState
    data class Success(val orders: List<Order>) : StaffArchiveUiState
    data class Error(val message: String) : StaffArchiveUiState
}
