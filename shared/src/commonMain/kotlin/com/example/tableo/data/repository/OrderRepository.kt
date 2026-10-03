package com.example.tableo.data.repository

import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.Order
import kotlinx.coroutines.flow.Flow

/** Zugriff auf die Bestellungen. */
interface OrderRepository {

    /** Liefert alle noch nicht erledigten Bestellungen eines Restaurants live als Flow. */
    fun getActiveOrders(restaurantId: String): Flow<List<Order>>

    /** Liefert alle erledigten Bestellungen eines Restaurants live als Flow (Archiv). */
    fun getOrderHistory(restaurantId: String): Flow<List<Order>>

    /** Liefert alle Bestellungen eines bestimmten Kunden live als Flow, unabhängig vom Restaurant. */
    fun getOrdersForUser(userId: String): Flow<List<Order>>

    /** Liefert alle Lieferungen eines Restaurants live als Flow, für die Fahrer. */
    fun getDeliveryOrders(restaurantId: String): Flow<List<Order>>

    /** Eine einzelne Bestellung live, z.B. fürs Tracking. null, wenn es sie nicht gibt. */
    fun getOrder(orderId: String): Flow<Order?>

    /** Legt eine neue Bestellung an und gibt die neue ID zurück. */
    suspend fun placeOrder(order: Order): Result<String>

    /** Ändert den Status einer bestehenden Bestellung. */
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>

    /** Speichert den aktuellen Standort des Fahrers an allen Bestellungen seiner Tour. */
    suspend fun updateDriverLocation(orderIds: List<String>, location: Coordinates): Result<Unit>

    /** Fahrer nimmt eine Lieferung in seine Tour. */
    suspend fun acceptDelivery(orderId: String, driverName: String, tourPosition: Int, status: OrderStatus): Result<Unit>

    /**
     * Storniert eine Bestellung. Der Kunde darf das nur, solange sie noch nicht zubereitet wird,
     * das Personal nur, bis der Fahrer losgefahren ist.
     *
     * @param byRestaurant true = Personal, false = Kunde
     */
    suspend fun cancelOrder(orderId: String, byRestaurant: Boolean): Result<Unit>

    /** Speichert die Reihenfolge der Tour, Bestell-ID -> Platz. */
    suspend fun updateTourPositions(positions: Map<String, Int>): Result<Unit>
}
