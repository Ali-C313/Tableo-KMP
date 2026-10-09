package com.example.tableo.ui.guest.restaurantList

import com.example.tableo.data.model.Restaurant

/** Zustand der Restaurant-Übersicht, je nachdem wie weit das Laden aus Firestore ist. */
sealed interface RestaurantListUiState {
    data object Loading : RestaurantListUiState
    data class Success(val restaurants: List<Restaurant>) : RestaurantListUiState
    data class Error(val message: String) : RestaurantListUiState
}
