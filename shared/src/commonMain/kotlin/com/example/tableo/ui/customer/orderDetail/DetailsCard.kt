package com.example.tableo.ui.customer.orderDetail

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.Order
import com.example.tableo.util.estimatedReadyTimeText
import com.example.tableo.util.firstName
import com.example.tableo.data.enums.label
import com.example.tableo.resources.Res
import com.example.tableo.resources.common_guest
import com.example.tableo.resources.delivery_mode_pickup
import com.example.tableo.resources.order_detail_cancelled_by_restaurant
import com.example.tableo.resources.order_detail_cancelled_by_you
import com.example.tableo.resources.order_detail_label_address
import com.example.tableo.resources.order_detail_label_cancelled_by
import com.example.tableo.resources.order_detail_label_driver
import com.example.tableo.resources.order_detail_label_estimated
import com.example.tableo.resources.order_detail_label_ordered_as
import com.example.tableo.resources.order_detail_label_payment
import com.example.tableo.resources.order_detail_label_type
import com.example.tableo.resources.order_detail_label_where_when
import com.example.tableo.resources.order_detail_type_dine_in
import com.example.tableo.resources.order_type_delivery
import com.example.tableo.resources.order_type_pickup_at
import com.example.tableo.resources.order_type_table
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.data.model.OrderItem
import com.example.tableo.data.enums.PaymentMethod

/** Bestellart, Adresse bzw. Tisch, Bezahlart, Fahrer und Fertigzeit. */
@Composable
fun DetailsCard(order: Order) {
    val typeText = when (order.orderType) {
        OrderType.DINE_IN -> stringResource(Res.string.order_detail_type_dine_in)
        OrderType.PICKUP -> stringResource(Res.string.delivery_mode_pickup)
        OrderType.DELIVERY -> stringResource(Res.string.order_type_delivery)
    }
    val placeText = when (order.orderType) {
        OrderType.DINE_IN -> stringResource(Res.string.order_type_table, order.tableNumber.toString())
        OrderType.PICKUP -> stringResource(Res.string.order_type_pickup_at, order.pickupTime.toString())
        OrderType.DELIVERY -> order.deliveryAddress.orEmpty()
    }

    DetailCard(modifier = Modifier.padding(top = 12.dp)) {
        DetailRow(stringResource(Res.string.order_detail_label_type), typeText)
        DetailRow(
            if (order.orderType == OrderType.DELIVERY) stringResource(Res.string.order_detail_label_address)
            else stringResource(Res.string.order_detail_label_where_when),
            placeText
        )
        DetailRow(stringResource(Res.string.order_detail_label_payment), order.paymentMethod.label)
        DetailRow(stringResource(Res.string.order_detail_label_ordered_as), order.customerName.ifBlank { stringResource(Res.string.common_guest) })
        order.driverName?.let { DetailRow(stringResource(Res.string.order_detail_label_driver), it.firstName()) }
        if (!order.status.isDone) {
            DetailRow(stringResource(Res.string.order_detail_label_estimated), order.estimatedReadyTimeText())
        }
        if (order.status == OrderStatus.STORNIERT) {
            DetailRow(
                stringResource(Res.string.order_detail_label_cancelled_by),
                if (order.cancelledByRestaurant) stringResource(Res.string.order_detail_cancelled_by_restaurant)
                else stringResource(Res.string.order_detail_cancelled_by_you)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailsCardPreview() {
    TableoTheme {
        DetailsCard(order = Order(
            id = "a1b2c3d4",
            restaurantId = "1",
            restaurantName = "Layali Lounge",
            customerName = "Sara",
            orderType = OrderType.DELIVERY,
            deliveryAddress = "Schanzenstraße 5, 20357 Hamburg",
            driverName = "Omar Haddad",
            paymentMethod = PaymentMethod.PAYPAL,
            items = listOf(OrderItem(menuItemId = "1", name = "Falafel-Teller", pricePerItem = 11.5, quantity = 2)),
            status = OrderStatus.UNTERWEGS
        ))
    }
}
