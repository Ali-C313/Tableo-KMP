package com.example.tableo.ui.customer.tracking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.navigation.Route
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Hört live auf die Lieferungen des Restaurants. Daraus kommen die eigene Bestellung und die
 * Lieferungen, die der Fahrer vorher noch macht (für die Ankunftszeit).
 */
class OrderTrackingViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val orderId: String = savedStateHandle.toRoute<Route.OrderTracking>().orderId

    private val _uiState = MutableStateFlow<OrderTrackingUiState>(OrderTrackingUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeOrder()
    }

    private fun observeOrder() {
        viewModelScope.launch {
            // einmal laden, um das Restaurant zu kennen
            val firstOrder = runCatching { orderRepository.getOrder(orderId).first() }.getOrNull()
            if (firstOrder == null) {
                _uiState.update { OrderTrackingUiState.Error("Bestellung nicht gefunden") }
                return@launch
            }

            // eine Abfrage für alles: die eigene Bestellung ist bei den Lieferungen dabei
            orderRepository.getDeliveryOrders(firstOrder.restaurantId)
                .catch { e -> _uiState.update { OrderTrackingUiState.Error(e.message ?: "Bestellung konnte nicht geladen werden") } }
                .collect { deliveries ->
                    val order = deliveries.find { it.id == orderId } ?: firstOrder
                    _uiState.update { OrderTrackingUiState.Success(order, stopsBefore(order, deliveries)) }
                }
        }
    }

    /** Adressen, die derselbe Fahrer vor dieser Bestellung noch anfährt. */
    private fun stopsBefore(order: Order, deliveries: List<Order>): List<Coordinates> {
        val position = order.tourPosition ?: return emptyList()
        return deliveries
            .filter { it.driverName == order.driverName && it.id != order.id }
            .filter { it.status == OrderStatus.ANGENOMMEN || it.status == OrderStatus.UNTERWEGS }
            .filter { (it.tourPosition ?: Int.MAX_VALUE) < position }
            .sortedBy { it.tourPosition }
            .mapNotNull { it.deliveryLocation }
    }
}

/** Zustand des Trackings. */
sealed interface OrderTrackingUiState {
    data object Loading : OrderTrackingUiState

    /** @param stopsBefore Adressen, die der Fahrer vorher noch beliefert */
    data class Success(val order: Order, val stopsBefore: List<Coordinates>) : OrderTrackingUiState
    data class Error(val message: String) : OrderTrackingUiState
}
