package com.example.tableo.ui.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.OrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.tableo.resources.Res
import com.example.tableo.resources.my_orders_cancel_failed
import com.example.tableo.resources.my_orders_load_failed
import com.example.tableo.resources.my_orders_not_logged_in
import org.jetbrains.compose.resources.getString

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

    /** Läuft, solange die Bestellungen live beobachtet werden. */
    private var ordersJob: Job? = null

    /** Lädt die Bestellungen des aktuell eingeloggten Kunden live, sortiert nach Datum. */
    fun loadOrders() {
        val userId = authRepository.currentUserId()
        if (userId == null) {
            viewModelScope.launch {
                val message = getString(Res.string.my_orders_not_logged_in)
                _uiState.update { MyOrdersUiState.Error(message) }
            }
            return
        }
        // beim erneuten Öffnen den alten Listener beenden, sonst laufen mehrere gleichzeitig
        ordersJob?.cancel()
        ordersJob = viewModelScope.launch {
            orderRepository.getOrdersForUser(userId)
                .catch { e -> _uiState.update { MyOrdersUiState.Error(e.message ?: getString(Res.string.my_orders_load_failed)) } }
                .collect { orders ->
                    _uiState.update { MyOrdersUiState.Success(orders.sortedByDescending { it.createdAt }) }
                }
        }
    }

    /** Kunde storniert seine Bestellung. Klappt nur, solange sie noch OFFEN ist. */
    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.cancelOrder(order.id, byRestaurant = false)
                .onFailure { e -> _cancelError.update { e.message ?: getString(Res.string.my_orders_cancel_failed) } }
        }
    }

    /** Blendet den Storno-Fehler wieder aus. */
    fun clearCancelError() {
        _cancelError.update { null }
    }
}
