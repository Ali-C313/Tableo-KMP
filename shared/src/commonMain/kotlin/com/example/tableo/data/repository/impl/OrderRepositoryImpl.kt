package com.example.tableo.data.repository.impl

import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.Order
import com.example.tableo.data.remote.FirestoreCollections
import com.example.tableo.data.repository.OrderRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Zugriff auf die Bestellungen in Firestore. */
class OrderRepositoryImpl(
    private val firestore: FirebaseFirestore
) : OrderRepository {

    private val orderCollection = firestore.collection(FirestoreCollections.ORDERS)

    // ERLEDIGT, STORNIERT und der alte Status BEZAHLT zählen als abgeschlossen
    private val doneStatuses = listOf(OrderStatus.ERLEDIGT.name, OrderStatus.BEZAHLT.name, OrderStatus.STORNIERT.name)

    override fun getActiveOrders(restaurantId: String): Flow<List<Order>> =
        orderCollection
            .where { ("restaurantId" equalTo restaurantId) and ("status" notInArray doneStatuses) }
            .snapshots
            .map { it.toOrders() }

    override fun getOrderHistory(restaurantId: String): Flow<List<Order>> =
        orderCollection
            .where { ("restaurantId" equalTo restaurantId) and ("status" inArray doneStatuses) }
            .snapshots
            .map { it.toOrders() }

    override fun getOrdersForUser(userId: String): Flow<List<Order>> =
        orderCollection
            .where { "userId" equalTo userId }
            .snapshots
            .map { it.toOrders() }

    override fun getDeliveryOrders(restaurantId: String): Flow<List<Order>> =
        // nur Gleichheits-Filter, braucht keinen extra Index. Status wird im ViewModel gefiltert
        orderCollection
            .where { ("restaurantId" equalTo restaurantId) and ("orderType" equalTo OrderType.DELIVERY.name) }
            .snapshots
            .map { it.toOrders() }

    override fun getOrder(orderId: String): Flow<Order?> =
        orderCollection.document(orderId).snapshots.map { it.toOrderOrNull() }

    override suspend fun placeOrder(order: Order): Result<String> = runCatching {
        // add legt das Dokument mit automatischer ID an
        orderCollection.add(order).id
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> = runCatching {
        orderCollection.document(orderId).update("status" to status.name)
    }

    override suspend fun updateDriverLocation(orderIds: List<String>, location: Coordinates): Result<Unit> = runCatching {
        // als einfache Map, so klappt es auf Android und iOS ohne eigenen Serializer
        val value = mapOf("latitude" to location.latitude, "longitude" to location.longitude)
        val batch = firestore.batch()
        orderIds.forEach { id -> batch.update(orderCollection.document(id), "driverLocation" to value) }
        batch.commit()
    }

    override suspend fun acceptDelivery(
        orderId: String,
        driverName: String,
        tourPosition: Int,
        status: OrderStatus
    ): Result<Unit> = runCatching {
        orderCollection.document(orderId).update(
            "driverName" to driverName,
            "tourPosition" to tourPosition,
            "status" to status.name
        )
    }

    override suspend fun cancelOrder(orderId: String, byRestaurant: Boolean): Result<Unit> = runCatching {
        val docRef = orderCollection.document(orderId)
        // Transaktion, damit der Status beim Stornieren nicht gleichzeitig weitergeschaltet wird
        val cancelled = firestore.runTransaction {
            val statusName = get(docRef).get<String?>("status")
            val status = OrderStatus.entries.find { it.name == statusName }
            val allowed = if (byRestaurant) status?.canRestaurantCancel == true else status == OrderStatus.OFFEN
            if (allowed) {
                update(docRef, "status" to OrderStatus.STORNIERT.name, "cancelledByRestaurant" to byRestaurant)
            }
            allowed
        }
        if (!cancelled) {
            error(
                if (byRestaurant) "Die Bestellung ist schon unterwegs und kann nicht mehr storniert werden"
                else "Die Bestellung wird schon zubereitet und kann nicht mehr storniert werden"
            )
        }
    }

    override suspend fun updateTourPositions(positions: Map<String, Int>): Result<Unit> = runCatching {
        val batch = firestore.batch()
        positions.forEach { (id, position) -> batch.update(orderCollection.document(id), "tourPosition" to position) }
        batch.commit()
    }
}
