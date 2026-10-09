package com.example.tableo.ui.staff.dashboard

import com.example.tableo.data.model.Order

/** Zustand des Bestell-Dashboards, je nachdem wie weit das Laden aus Firestore ist. */
sealed interface StaffDashboardUiState {
    data object Loading : StaffDashboardUiState
    data class Success(val orders: List<Order>) : StaffDashboardUiState
    data class Error(val message: String) : StaffDashboardUiState
}
