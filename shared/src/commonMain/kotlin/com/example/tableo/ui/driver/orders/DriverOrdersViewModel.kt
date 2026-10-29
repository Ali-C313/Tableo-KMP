package com.example.tableo.ui.driver.orders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.LocationRepository
import com.example.tableo.data.repository.NotificationRepository
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.navigation.Route
import com.example.tableo.resources.Res
import com.example.tableo.resources.common_guest
import com.example.tableo.resources.driver_error_load
import com.example.tableo.resources.driver_error_location_denied
import com.example.tableo.resources.driver_error_location_send
import com.example.tableo.resources.driver_notification_new_text
import com.example.tableo.resources.driver_notification_new_title
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Fahrer-Bereich: abholbereite Lieferungen annehmen, Reihenfolge der eigenen Tour festlegen
 * und die Tour starten. Ab der ersten angenommenen Lieferung geht der GPS-Standort an alle
 * Bestellungen der Tour, damit die Kunden den Fahrer auf der Karte sehen.
 * Kommt eine neue Lieferung rein, gibt es einmal eine Benachrichtigung.
 */
class DriverOrdersViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository,
    private val locationRepository: LocationRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.DriverOrders>()
    private val restaurantId: String = route.restaurantId
    val restaurantName: String = route.restaurantName
    val driverName: String = route.driverName

    private val _uiState = MutableStateFlow<DriverOrdersUiState>(DriverOrdersUiState.Loading)
    val uiState = _uiState.asStateFlow()

    /** true, solange der Standort gesendet wird. */
    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _trackingError = MutableStateFlow<String?>(null)
    val trackingError = _trackingError.asStateFlow()

    private var trackingJob: Job? = null

    // null = noch nichts geladen, damit beim Öffnen nicht für alle alten Lieferungen eine Nachricht kommt
    private var knownAvailableIds: Set<String>? = null

    // jede Lieferung wird nur einmal gemeldet
    private val notifiedIds = mutableSetOf<String>()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            orderRepository.getDeliveryOrders(restaurantId)
                .catch { e -> _uiState.update { DriverOrdersUiState.Error(e.message ?: getString(Res.string.driver_error_load)) } }
                .collect { orders ->
                    val available = orders
                        .filter { it.status == OrderStatus.ABHOLBEREIT }
                        .sortedBy { it.createdAt }
                    val tour = orders
                        .filter { it.driverName == driverName }
                        .filter { it.status == OrderStatus.ANGENOMMEN || it.status == OrderStatus.UNTERWEGS }
                        .sortedBy { it.tourPosition ?: Int.MAX_VALUE }

                    notifyAboutNewDeliveries(available)
                    _uiState.update { DriverOrdersUiState.Success(available, tour) }
                }
        }
    }

    /**
     * Lieferung in die eigene Tour nehmen, kommt hinten dran. Ab jetzt wird der Standort gesendet.
     *
     * @param sendLocation false, wenn der Fahrer die Standort-Freigabe abgelehnt hat
     */
    fun accept(order: Order, sendLocation: Boolean = true) {
        val tour = currentTour()
        val position = (tour.maxOfOrNull { it.tourPosition ?: 0 } ?: 0) + 1
        // läuft die Tour schon, ist die neue Lieferung direkt mit unterwegs
        val status = if (tour.any { it.status == OrderStatus.UNTERWEGS }) OrderStatus.UNTERWEGS else OrderStatus.ANGENOMMEN
        viewModelScope.launch {
            orderRepository.acceptDelivery(order.id, driverName, position, status)
        }
        if (sendLocation) startTracking()
    }

    /** Lieferung in der Tour einen Platz nach vorne (-1) oder hinten (+1) schieben. */
    fun move(order: Order, direction: Int) {
        val tour = currentTour().toMutableList()
        val index = tour.indexOfFirst { it.id == order.id }
        val newIndex = index + direction
        if (index == -1 || newIndex !in tour.indices) return

        tour.removeAt(index)
        tour.add(newIndex, order)
        savePositions(tour)
    }

    /** Alle angenommenen Lieferungen auf unterwegs setzen und Standort senden. Klappt auch zum Fortsetzen. */
    fun startTour() {
        viewModelScope.launch {
            currentTour()
                .filter { it.status == OrderStatus.ANGENOMMEN }
                .forEach { orderRepository.updateOrderStatus(it.id, OrderStatus.UNTERWEGS) }
        }
        startTracking()
    }

    /** Lieferung ist angekommen. Die nächste rückt auf Platz 1, deren Kunde sieht dann die Karte. */
    fun finishDelivery(order: Order) {
        val remaining = currentTour().filter { it.id != order.id }
        viewModelScope.launch {
            orderRepository.updateOrderStatus(order.id, OrderStatus.ERLEDIGT)
        }
        savePositions(remaining)
        if (remaining.isEmpty()) stopTracking()
    }

    /** Ohne Standort-Freigabe gibt es einen Hinweis, dass Kunden den Fahrer nicht sehen. */
    fun onLocationPermissionDenied() {
        viewModelScope.launch {
            _trackingError.update { getString(Res.string.driver_error_location_denied) }
        }
    }

    private fun currentTour(): List<Order> =
        (_uiState.value as? DriverOrdersUiState.Success)?.tour ?: emptyList()

    /** Schreibt die Plätze 1, 2, 3 ... in der Reihenfolge der Liste. */
    private fun savePositions(tour: List<Order>) {
        if (tour.isEmpty()) return
        val positions = tour.mapIndexed { index, order -> order.id to index + 1 }.toMap()
        viewModelScope.launch {
            orderRepository.updateTourPositions(positions)
        }
    }

    private fun startTracking() {
        if (trackingJob != null) return
        _trackingError.update { null }
        _isTracking.update { true }
        trackingJob = viewModelScope.launch {
            locationRepository.locationUpdates()
                .catch { e ->
                    _trackingError.update { e.message ?: getString(Res.string.driver_error_location_send) }
                    _isTracking.update { false }
                    trackingJob = null
                }
                .collect { location ->
                    // Standort geht an alle Lieferungen der Tour, auch an die noch nicht gestarteten
                    val ids = currentTour().map { it.id }
                    if (ids.isNotEmpty()) orderRepository.updateDriverLocation(ids, location)
                }
        }
    }

    private fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
        _isTracking.update { false }
    }

    private suspend fun notifyAboutNewDeliveries(available: List<Order>) {
        val ids = available.map { it.id }.toSet()
        val known = knownAvailableIds
        knownAvailableIds = ids
        if (known == null) return

        available.filter { it.id !in known && notifiedIds.add(it.id) }.forEach { order ->
            notificationRepository.show(
                id = order.id.hashCode(),
                title = getString(Res.string.driver_notification_new_title),
                text = getString(
                    Res.string.driver_notification_new_text,
                    order.customerName.ifBlank { getString(Res.string.common_guest) },
                    order.deliveryAddress.orEmpty()
                )
            )
        }
    }
}
