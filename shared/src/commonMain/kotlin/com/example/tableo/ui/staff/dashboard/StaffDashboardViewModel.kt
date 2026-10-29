package com.example.tableo.ui.staff.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.tableo.resources.Res
import com.example.tableo.resources.dashboard_error_load
import org.jetbrains.compose.resources.getString

/** Offene Bestellungen eines Restaurants und ihr Status. */
class StaffDashboardViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.StaffDashboard>()
    private val restaurantId: String = route.restaurantId
    val restaurantName: String = route.restaurantName

    private val _uiState = MutableStateFlow<StaffDashboardUiState>(StaffDashboardUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    /** Lädt die aktiven Bestellungen des Restaurants aus der Route. */
    private fun loadOrders() {
        viewModelScope.launch {
            orderRepository.getActiveOrders(restaurantId)
                .catch { e ->
                    _uiState.update { StaffDashboardUiState.Error(e.message ?: getString(Res.string.dashboard_error_load)) }
                }
                .collect { orders ->
                    // was noch in der Küche ist zuerst, Lieferungen bei den Fahrern ans Ende
                    val sorted = orders.sortedWith(compareBy<Order>({ it.status.isWithDriver }, { it.createdAt }))
                    _uiState.update { StaffDashboardUiState.Success(sorted) }
                }
        }
    }

    /** Setzt eine Bestellung auf den jeweils nächsten Bearbeitungsschritt. */
    fun advanceStatus(order: Order) {
        // ab FERTIG übernimmt bei Lieferungen der Fahrer, sonst wird direkt erledigt
        val nextStatus = when (order.status) {
            OrderStatus.OFFEN -> OrderStatus.IN_ZUBEREITUNG
            OrderStatus.IN_ZUBEREITUNG -> OrderStatus.FERTIG
            else -> return
        }
        viewModelScope.launch {
            orderRepository.updateOrderStatus(order.id, nextStatus)
        }
    }

    /** Personal storniert, geht in jedem Schritt. Die Bestellung landet dann im Archiv. */
    fun cancelOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.cancelOrder(order.id, byRestaurant = true)
        }
    }

    /**
     * Personal ist mit der Bestellung fertig. Lieferungen gehen an die Fahrer (die bekommen
     * eine Nachricht), alles andere landet im Archiv.
     */
    fun markAsDone(order: Order) {
        val newStatus = if (order.orderType == OrderType.DELIVERY) OrderStatus.ABHOLBEREIT else OrderStatus.ERLEDIGT
        viewModelScope.launch {
            orderRepository.updateOrderStatus(order.id, newStatus)
        }
    }
}
