package com.example.tableo.ui.customer.tracking

import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.Order

/** Zustand des Trackings. */
sealed interface OrderTrackingUiState {
    data object Loading : OrderTrackingUiState

    /** @param stopsBefore Adressen, die der Fahrer vorher noch beliefert */
    data class Success(val order: Order, val stopsBefore: List<Coordinates>) : OrderTrackingUiState
    data class Error(val message: String) : OrderTrackingUiState
}
