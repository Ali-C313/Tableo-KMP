package com.example.tableo.ui.driver.orders

import com.example.tableo.data.model.Order

/** Zustand des Fahrer-Bereichs. */
sealed interface DriverOrdersUiState {
    data object Loading : DriverOrdersUiState

    /**
     * @param available Lieferungen, die noch kein Fahrer hat
     * @param tour Eigene Lieferungen, sortiert nach Platz in der Tour
     */
    data class Success(val available: List<Order>, val tour: List<Order>) : DriverOrdersUiState
    data class Error(val message: String) : DriverOrdersUiState
}
