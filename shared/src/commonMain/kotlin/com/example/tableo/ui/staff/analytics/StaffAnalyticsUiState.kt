package com.example.tableo.ui.staff.analytics

import com.example.tableo.data.model.ChartBucket

/** Zustand des Statistik-Screens. */
sealed interface StaffAnalyticsUiState {
    data object Loading : StaffAnalyticsUiState
    data class Success(
        val buckets: List<ChartBucket>,
        val totalRevenue: Double,
        val totalOrders: Int
    ) : StaffAnalyticsUiState
    data class Error(val message: String) : StaffAnalyticsUiState
}
