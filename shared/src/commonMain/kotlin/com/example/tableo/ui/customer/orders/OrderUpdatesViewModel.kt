package com.example.tableo.ui.customer.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.NotificationRepository
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.util.FormatHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.example.tableo.resources.Res
import com.example.tableo.resources.notification_cancelled_text
import com.example.tableo.resources.notification_cancelled_title
import com.example.tableo.resources.notification_delivered_text
import com.example.tableo.resources.notification_delivered_title
import com.example.tableo.resources.notification_driver_found_text
import com.example.tableo.resources.notification_driver_found_title
import com.example.tableo.resources.notification_on_the_way_text
import com.example.tableo.resources.notification_on_the_way_title
import com.example.tableo.resources.notification_queued_text
import com.example.tableo.resources.notification_queued_title
import com.example.tableo.resources.notification_ready_pickup_text
import com.example.tableo.resources.notification_ready_table_text
import com.example.tableo.resources.notification_ready_title
import com.example.tableo.resources.tracking_driver_fallback
import org.jetbrains.compose.resources.getString

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

    private suspend fun checkForUpdate(order: Order) {
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
    private suspend fun messageFor(order: Order, step: String): Pair<String, String>? {
        // aus Datenschutzgründen nur der Vorname
        val driver = order.driverName?.let { FormatHelper.firstName(it) } ?: getString(Res.string.tracking_driver_fallback)
        val ordersBefore = (order.tourPosition ?: 1) - 1

        return when {
            step == "UNTERWEGS_NAECHSTER" ->
                getString(Res.string.notification_on_the_way_title) to getString(Res.string.notification_on_the_way_text, driver)

            step.startsWith("UNTERWEGS_PLATZ_") ->
                getString(Res.string.notification_queued_title) to getString(Res.string.notification_queued_text, driver, ordersBefore)

            order.status == OrderStatus.ANGENOMMEN ->
                getString(Res.string.notification_driver_found_title) to getString(Res.string.notification_driver_found_text, driver, order.restaurantName)

            order.status == OrderStatus.FERTIG && order.orderType == OrderType.PICKUP ->
                getString(Res.string.notification_ready_title) to getString(Res.string.notification_ready_pickup_text, order.restaurantName)

            order.status == OrderStatus.FERTIG && order.orderType == OrderType.DINE_IN ->
                getString(Res.string.notification_ready_title) to getString(Res.string.notification_ready_table_text, order.tableNumber.toString())

            // selbst storniert braucht keine Nachricht, nur wenn das Restaurant storniert
            order.status == OrderStatus.STORNIERT && order.cancelledByRestaurant ->
                getString(Res.string.notification_cancelled_title) to getString(Res.string.notification_cancelled_text, order.restaurantName)

            order.status == OrderStatus.ERLEDIGT && order.orderType == OrderType.DELIVERY ->
                getString(Res.string.notification_delivered_title) to getString(Res.string.notification_delivered_text)

            else -> null
        }
    }
}
