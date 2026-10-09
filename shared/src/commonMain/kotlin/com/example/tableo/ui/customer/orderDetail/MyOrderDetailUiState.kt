package com.example.tableo.ui.customer.orderDetail

import com.example.tableo.data.model.Order

/** Zustand der Detailansicht. */
sealed interface MyOrderDetailUiState {
    data object Loading : MyOrderDetailUiState
    data class Success(val order: Order) : MyOrderDetailUiState
    data class Error(val message: String) : MyOrderDetailUiState
}
