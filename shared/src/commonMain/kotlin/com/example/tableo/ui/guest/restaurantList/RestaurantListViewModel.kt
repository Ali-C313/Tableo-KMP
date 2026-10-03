package com.example.tableo.ui.guest.restaurantList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Lädt alle Restaurants live aus Firestore. */
class RestaurantListViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RestaurantListUiState>(RestaurantListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeRestaurants()
    }

    // hört dauerhaft auf Firestore-Änderungen (Live-Liste)
    private fun observeRestaurants() {
        viewModelScope.launch {
            restaurantRepository.getRestaurants()
                .catch { e ->
                    _uiState.update { RestaurantListUiState.Error(e.message ?: "Restaurants konnten nicht geladen werden") }
                }
                .collect { restaurants ->
                    _uiState.update { RestaurantListUiState.Success(restaurants) }
                }
        }
    }
}

/** Zustand der Restaurant-Übersicht, je nachdem wie weit das Laden aus Firestore ist. */
sealed interface RestaurantListUiState {
    data object Loading : RestaurantListUiState
    data class Success(val restaurants: List<Restaurant>) : RestaurantListUiState
    data class Error(val message: String) : RestaurantListUiState
}