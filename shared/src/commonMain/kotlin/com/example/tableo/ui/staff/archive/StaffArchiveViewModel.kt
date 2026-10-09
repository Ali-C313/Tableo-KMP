package com.example.tableo.ui.staff.archive

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.tableo.resources.Res
import com.example.tableo.resources.archive_error_load
import org.jetbrains.compose.resources.getString

/** Lädt die abgeschlossenen Bestellungen, neueste zuerst. */
class StaffArchiveViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.StaffArchive>()
    val restaurantId: String = route.restaurantId
    val restaurantName: String = route.restaurantName

    private val _uiState = MutableStateFlow<StaffArchiveUiState>(StaffArchiveUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            orderRepository.getOrderHistory(restaurantId)
                .catch { e -> _uiState.update { StaffArchiveUiState.Error(e.message ?: getString(Res.string.archive_error_load)) } }
                .collect { orders ->
                    _uiState.update { StaffArchiveUiState.Success(orders.sortedByDescending { it.createdAt }) }
                }
        }
    }
}
