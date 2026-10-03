package com.example.tableo.ui.guest.menu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tableo.data.model.MenuItem
import com.example.tableo.data.repository.MenuRepository
import com.example.tableo.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Lädt die Speisekarte eines Restaurants. */
class MenuViewModel(
    savedStateHandle: SavedStateHandle,
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.GuestMenu>()
    val restaurantId: String = route.restaurantId
    val restaurantName: String = route.restaurantName

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadMenu()
    }

    /** Lädt das Menü des Restaurants aus der Route. */
    private fun loadMenu() {
        viewModelScope.launch {
            menuRepository.getMenuItems(restaurantId)
                .catch { e ->
                    _uiState.update { MenuUiState.Error(e.message ?: "Menü konnte nicht geladen werden") }
                }
                .collect { items ->
                    _uiState.update { MenuUiState.Success(items) }
                }
        }
    }
}

/** Zustand des Menü-Screens, je nachdem wie weit das Laden aus Firestore ist. */
sealed interface MenuUiState {
    data object Loading : MenuUiState
    data class Success(val items: List<MenuItem>) : MenuUiState
    data class Error(val message: String) : MenuUiState
}