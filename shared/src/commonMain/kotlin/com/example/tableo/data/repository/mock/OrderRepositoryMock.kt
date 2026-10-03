package com.example.tableo.data.repository.mock

import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.enums.PaymentMethod
import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.Order
import com.example.tableo.data.model.OrderItem
import com.example.tableo.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/** Testdaten für Previews, ohne Firestore. */
class OrderRepositoryMock : OrderRepository {

    private val orders = MutableStateFlow(
        listOf(
            Order(
                id = "1", restaurantId = "1", restaurantName = "Layali Lounge",
                orderType = OrderType.DINE_IN, tableNumber = 7,
                items = listOf(OrderItem(menuItemId = "1", name = "Doppelapfel", pricePerItem = 12.0, quantity = 1)),
                status = OrderStatus.OFFEN
            ),
            Order(
                id = "2", restaurantId = "1", restaurantName = "Layali Lounge",
                orderType = OrderType.PICKUP, pickupTime = "19:30",
                items = listOf(OrderItem(menuItemId = "2", name = "Minztee", pricePerItem = 4.5, quantity = 2)),
                status = OrderStatus.ERLEDIGT
            ),
            Order(
                id = "3", restaurantId = "1", restaurantName = "Layali Lounge",
                orderType = OrderType.DELIVERY,
                customerName = "Sara",
                deliveryAddress = "Schanzenstraße 5, 20357 Hamburg",
                deliveryLocation = Coordinates(53.5631, 9.9626),
                driverLocation = Coordinates(53.5500, 9.9600),
                driverName = "Omar", tourPosition = 1,
                paymentMethod = PaymentMethod.PAYPAL,
                items = listOf(OrderItem(menuItemId = "3", name = "Falafel-Teller", pricePerItem = 11.5, quantity = 2)),
                status = OrderStatus.UNTERWEGS
            ),
            Order(
                id = "4", restaurantId = "1", restaurantName = "Layali Lounge",
                customerName = "Jonas",
                orderType = OrderType.DELIVERY,
                deliveryAddress = "Reeperbahn 40, 20359 Hamburg",
                deliveryLocation = Coordinates(53.5496, 9.9600),
                paymentMethod = PaymentMethod.BAR,
                items = listOf(OrderItem(menuItemId = "1", name = "Doppelapfel", pricePerItem = 12.0, quantity = 1)),
                status = OrderStatus.ABHOLBEREIT
            )
        )
    )

    override fun getActiveOrders(restaurantId: String) =
        orders.map { list -> list.filter { it.restaurantId == restaurantId && !it.status.isDone } }

    override fun getOrderHistory(restaurantId: String) =
        orders.map { list -> list.filter { it.restaurantId == restaurantId && it.status.isDone } }

    override fun getOrdersForUser(userId: String) = orders.asStateFlow()

    override fun getDeliveryOrders(restaurantId: String) =
        orders.map { list -> list.filter { it.restaurantId == restaurantId && it.orderType == OrderType.DELIVERY } }

    override fun getOrder(orderId: String) =
        orders.map { list -> list.find { it.id == orderId } }

    override suspend fun placeOrder(order: Order): Result<String> = runCatching {
        val newId = "${orders.value.size + 1}"
        orders.value = orders.value + order.copy(id = newId)
        newId
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> = runCatching {
        orders.value = orders.value.map { if (it.id == orderId) it.copy(status = status) else it }
    }

    override suspend fun updateDriverLocation(orderIds: List<String>, location: Coordinates): Result<Unit> = runCatching {
        orders.value = orders.value.map { if (it.id in orderIds) it.copy(driverLocation = location) else it }
    }

    override suspend fun acceptDelivery(
        orderId: String,
        driverName: String,
        tourPosition: Int,
        status: OrderStatus
    ): Result<Unit> = runCatching {
        orders.value = orders.value.map {
            if (it.id == orderId) it.copy(driverName = driverName, tourPosition = tourPosition, status = status) else it
        }
    }

    override suspend fun cancelOrder(orderId: String, byRestaurant: Boolean): Result<Unit> = runCatching {
        val status = orders.value.find { it.id == orderId }?.status
        val allowed = if (byRestaurant) status?.canRestaurantCancel == true else status == OrderStatus.OFFEN
        check(allowed) { "Bestellung kann nicht mehr storniert werden" }
        orders.value = orders.value.map {
            if (it.id == orderId) it.copy(status = OrderStatus.STORNIERT, cancelledByRestaurant = byRestaurant) else it
        }
    }

    override suspend fun updateTourPositions(positions: Map<String, Int>): Result<Unit> = runCatching {
        orders.value = orders.value.map { order ->
            positions[order.id]?.let { order.copy(tourPosition = it) } ?: order
        }
    }
}
