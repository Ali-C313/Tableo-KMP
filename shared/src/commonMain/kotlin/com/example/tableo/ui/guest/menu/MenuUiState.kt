package com.example.tableo.ui.guest.menu

import com.example.tableo.data.model.MenuItem

/** Zustand des Menü-Screens, je nachdem wie weit das Laden aus Firestore ist. */
sealed interface MenuUiState {
    data object Loading : MenuUiState
    data class Success(val items: List<MenuItem>) : MenuUiState
    data class Error(val message: String) : MenuUiState
}
