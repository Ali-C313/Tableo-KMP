package com.example.tableo.ui.staff.shift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.model.Order
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.NotificationRepository
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.data.repository.StaffRepository
import com.example.tableo.ui.util.asEuro
import com.example.tableo.ui.util.orderNumber
import com.example.tableo.ui.util.orderTypeLabel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Dienst-Schalter für Mitarbeiter. Solange jemand im Dienst ist, bekommt er eine
 * Nachricht bei jeder neuen Bestellung seines Restaurants. Läuft als single in Koin mit.
 */
class StaffShiftViewModel(
    private val orderRepository: OrderRepository,
    private val staffRepository: StaffRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    /** Eigenes Personal-Profil, null bei Kunden. */
    private val _profile = MutableStateFlow<StaffProfile?>(null)
    val profile = _profile.asStateFlow()

    private var profileJob: Job? = null
    private var ordersJob: Job? = null
    private var observedUserId: String? = null
    private var ordersRestaurantId: String? = null

    // schon gesehene Bestellungen, damit jede nur einmal gemeldet wird
    private val knownOrderIds = mutableSetOf<String>()

    /** Beobachtet das Profil des angemeldeten Kontos. Mehrfach aufrufen ist kein Problem. */
    fun start() {
        val userId = authRepository.currentUserId()
        if (userId == observedUserId) return
        stop()
        if (userId == null) return

        observedUserId = userId
        profileJob = viewModelScope.launch {
            staffRepository.observeProfileForUser(userId)
                .catch { }
                .collect { profile ->
                    _profile.update { profile }
                    if (profile != null && profile.onDuty) {
                        listenForOrders(profile.restaurantId)
                    } else {
                        stopListening()
                    }
                }
        }
    }

    /** Beim Abmelden aufrufen. Wer noch im Dienst war, wird dabei ausgetragen. */
    fun stop() {
        _profile.value?.takeIf { it.onDuty }?.let { profile ->
            viewModelScope.launch { staffRepository.setOnDuty(profile.id, false) }
        }
        profileJob?.cancel()
        profileJob = null
        observedUserId = null
        _profile.update { null }
        stopListening()
    }

    fun setOnDuty(onDuty: Boolean) {
        val profile = _profile.value ?: return
        // Schalter sofort umlegen, Firestore meldet den Stand danach sowieso nochmal
        _profile.update { profile.copy(onDuty = onDuty) }
        viewModelScope.launch {
            staffRepository.setOnDuty(profile.id, onDuty)
                .onFailure { _profile.update { profile } }
        }
    }

    private fun listenForOrders(restaurantId: String) {
        if (ordersJob != null && ordersRestaurantId == restaurantId) return
        stopListening()
        ordersRestaurantId = restaurantId

        var firstLoad = true
        ordersJob = viewModelScope.launch {
            orderRepository.getActiveOrders(restaurantId)
                .catch { } // Nachrichten sind nur ein Extra, Fehler zeigt das Dashboard selbst an
                .collect { orders ->
                    orders.forEach { order ->
                        val isNew = knownOrderIds.add(order.id)
                        // beim ersten Laden nur merken, sonst klingelt es für alle offenen Bestellungen
                        if (isNew && !firstLoad && order.status == OrderStatus.OFFEN) notifyNewOrder(order)
                    }
                    firstLoad = false
                }
        }
    }

    private fun stopListening() {
        ordersJob?.cancel()
        ordersJob = null
        ordersRestaurantId = null
        knownOrderIds.clear()
    }

    private fun notifyNewOrder(order: Order) {
        notificationRepository.show(
            id = order.id.hashCode(),
            title = "Neue Bestellung ${order.orderNumber()}",
            text = "${order.orderTypeLabel()} · ${order.customerName.ifBlank { "Gast" }} · ${order.totalPrice.asEuro()}"
        )
    }
}
