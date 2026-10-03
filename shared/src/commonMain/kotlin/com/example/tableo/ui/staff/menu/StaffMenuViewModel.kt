package com.example.tableo.ui.staff.menu

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

/** Speisekarte eines Restaurants fürs Personal: laden, ein-/ausblenden, löschen. */
class StaffMenuViewModel(
    savedStateHandle: SavedStateHandle,
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.StaffMenuManagement>()
    val restaurantId: String = route.restaurantId
    val restaurantName: String = route.restaurantName

    private val _uiState = MutableStateFlow<StaffMenuUiState>(StaffMenuUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadMenu()
    }

    private fun loadMenu() {
        viewModelScope.launch {
            menuRepository.getMenuItems(restaurantId)
                .catch { e -> _uiState.update { StaffMenuUiState.Error(e.message ?: "Menü konnte nicht geladen werden") } }
                .collect { items -> _uiState.update { StaffMenuUiState.Success(items) } }
        }
    }

    /** Schaltet die Verfügbarkeit eines Artikels um (z.B. wenn etwas ausverkauft ist). */
    fun setAvailability(menuItemId: String, isAvailable: Boolean) {
        viewModelScope.launch { menuRepository.setAvailability(menuItemId, isAvailable) }
    }

    /** Löscht einen Artikel endgültig aus der Speisekarte. */
    fun deleteItem(menuItemId: String) {
        viewModelScope.launch { menuRepository.deleteMenuItem(menuItemId) }
    }
}

/** Zustand der Speisekarten-Verwaltung fürs Personal. */
sealed interface StaffMenuUiState {
    data object Loading : StaffMenuUiState
    data class Success(val items: List<MenuItem>) : StaffMenuUiState
    data class Error(val message: String) : StaffMenuUiState
}