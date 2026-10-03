package com.example.tableo.ui.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.NotificationRepository
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.ui.util.firstName
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Beobachtet im Hintergrund die Bestellungen des angemeldeten Kunden und schickt eine
 * Benachrichtigung, wenn sich etwas Wichtiges ändert, z.B. "Deine Bestellung ist unterwegs".
 * Läuft als single in Koin, solange die App offen ist.
 */
class OrderUpdatesViewModel(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private var job: Job? = null
    private var observedUserId: String? = null

    // letzter Schritt pro Bestellung. Der Fahrer-Standort ändert sich alle paar Sekunden,
    // deshalb wird nur bei einem neuen Schritt benachrichtigt, nicht bei jeder Änderung
    private val lastStep = mutableMapOf<String, String>()

    /** Startet die Beobachtung für den eingeloggten Kunden. Mehrfach aufrufen ist kein Problem. */
    fun start() {
        val userId = authRepository.currentUserId()
        if (userId == observedUserId) return
        stop()
        if (userId == null) return

        observedUserId = userId
        job = viewModelScope.launch {
            orderRepository.getOrdersForUser(userId)
                .catch { } // Benachrichtigungen sind nur ein Extra, Fehler zeigt die Bestellliste selbst an
                .collect { orders -> orders.forEach { checkForUpdate(it) } }
        }
    }

    /** Beim Abmelden aufrufen. */
    fun stop() {
        job?.cancel()
        job = null
        observedUserId = null
        lastStep.clear()
    }

    private fun checkForUpdate(order: Order) {
        val step = stepOf(order)
        val before = lastStep.put(order.id, step)
        // beim ersten Laden nur merken, und nur melden, wenn sich der Schritt wirklich geändert hat
        if (before == null || before == step) return

        val (title, text) = messageFor(order, step) ?: return
        notificationRepository.show(order.id.hashCode(), title, text)
    }

    /** In welchem Schritt die Bestellung gerade ist, z.B. "UNTERWEGS_NAECHSTER". */
    private fun stepOf(order: Order): String {
        val position = order.tourPosition ?: 1
        return when {
            order.status == OrderStatus.UNTERWEGS && position <= 1 -> "UNTERWEGS_NAECHSTER"
            order.status == OrderStatus.UNTERWEGS -> "UNTERWEGS_PLATZ_$position"
            else -> order.status.name
        }
    }

    /** Titel und Text der Benachrichtigung, oder null wenn es für diesen Schritt keine gibt. */
    private fun messageFor(order: Order, step: String): Pair<String, String>? {
        // aus Datenschutzgründen nur der Vorname
        val driver = order.driverName?.firstName() ?: "Der Fahrer"
        val ordersBefore = (order.tourPosition ?: 1) - 1

        return when {
            step == "UNTERWEGS_NAECHSTER" ->
                "Deine Bestellung ist unterwegs" to "$driver ist jetzt auf dem Weg zu dir. Verfolge ihn live in der App."

            step.startsWith("UNTERWEGS_PLATZ_") ->
                "Fahrer ist unterwegs" to "$driver liefert vorher noch $ordersBefore Bestellung(en), dann bist du dran."

            order.status == OrderStatus.ANGENOMMEN ->
                "Fahrer gefunden" to "$driver bringt dir gleich deine Bestellung von ${order.restaurantName}."

            order.status == OrderStatus.FERTIG && order.orderType == OrderType.PICKUP ->
                "Deine Bestellung ist fertig" to "Du kannst sie jetzt bei ${order.restaurantName} abholen."

            order.status == OrderStatus.FERTIG && order.orderType == OrderType.DINE_IN ->
                "Deine Bestellung ist fertig" to "Sie kommt gleich an Tisch ${order.tableNumber}."

            // selbst storniert braucht keine Nachricht, nur wenn das Restaurant storniert
            order.status == OrderStatus.STORNIERT && order.cancelledByRestaurant ->
                "Bestellung storniert" to "${order.restaurantName} hat deine Bestellung storniert."

            order.status == OrderStatus.ERLEDIGT && order.orderType == OrderType.DELIVERY ->
                "Zugestellt" to "Guten Appetit!"

            else -> null
        }
    }
}
