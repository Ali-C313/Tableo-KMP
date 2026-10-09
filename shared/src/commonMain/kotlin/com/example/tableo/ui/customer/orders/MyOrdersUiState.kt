package com.example.tableo.ui.customer.orders

import com.example.tableo.data.model.Order

/** Zustand der Bestellhistorie. */
sealed interface MyOrdersUiState {
    data object Loading : MyOrdersUiState
    data class Success(val orders: List<Order>) : MyOrdersUiState
    data class Error(val message: String) : MyOrdersUiState
}
