package com.example.tableo.ui.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Lädt die Bestellungen des eingeloggten Kunden live. */
class MyOrdersViewModel(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyOrdersUiState>(MyOrdersUiState.Loading)
    val uiState = _uiState.asStateFlow()

    /** Fehlertext, wenn das Stornieren nicht geklappt hat, z.B. weil schon zubereitet wird. */
    private val _cancelError = MutableStateFlow<String?>(null)
    val cancelError = _cancelError.asStateFlow()

    /** Lädt die Bestellungen des aktuell eingeloggten Kunden live, sortiert nach Datum. */
    fun loadOrders() {
        val userId = authRepository.currentUserId()
        if (userId == null) {
            _uiState.update { MyOrdersUiState.Error("Nicht eingeloggt") }
            return
        }
        viewModelScope.launch {
            orderRepository.getOrdersForUser(userId)
                .catch { e -> _uiState.update { MyOrdersUiState.Error(e.message ?: "Bestellungen konnten nicht geladen werden") } }
                .collect { orders ->
                    _uiState.update { MyOrdersUiState.Success(orders.sortedByDescending { it.createdAt }) }
                }
        }
    }

    /** Kunde storniert seine Bestellung. Klappt nur, solange sie noch OFFEN ist. */
    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.cancelOrder(order.id, byRestaurant = false)
                .onFailure { e -> _cancelError.update { e.message ?: "Stornieren hat nicht geklappt" } }
        }
    }

    fun clearCancelError() {
        _cancelError.update { null }
    }
}

/** Zustand der Bestellhistorie. */
sealed interface MyOrdersUiState {
    data object Loading : MyOrdersUiState
    data class Success(val orders: List<Order>) : MyOrdersUiState
    data class Error(val message: String) : MyOrdersUiState
}