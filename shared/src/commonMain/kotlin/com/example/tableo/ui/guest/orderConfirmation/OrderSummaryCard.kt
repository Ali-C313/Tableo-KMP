package com.example.tableo.ui.guest.orderConfirmation

import com.example.tableo.resources.Res
import com.example.tableo.resources.order_delivery_to
import com.example.tableo.resources.order_estimated_label
import com.example.tableo.resources.order_payment
import com.example.tableo.resources.order_pickup_at
import com.example.tableo.resources.order_table
import com.example.tableo.resources.order_total
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.Order
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.util.FormatHelper
import com.example.tableo.util.OrderHelper
import org.jetbrains.compose.resources.stringResource
import com.example.tableo.data.enums.label
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.data.model.OrderItem
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.PaymentMethod

/** Bestellübersicht: geschätzte Zeit oben, darunter Bestellart, Artikel und Gesamtsumme. */
@Composable
fun OrderSummaryCard(order: Order) {
    val orderTypeText = when (order.orderType) {
        OrderType.DINE_IN -> stringResource(Res.string.order_table, order.tableNumber.toString())
        OrderType.PICKUP -> stringResource(Res.string.order_pickup_at, order.pickupTime.orEmpty())
        OrderType.DELIVERY -> stringResource(Res.string.order_delivery_to, order.deliveryAddress.orEmpty())
    }

    Card(
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(Res.string.order_estimated_label),                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = OrderHelper.estimatedReadyTimeText(order),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = OrderHelper.estimatedHint(order),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(orderTypeText, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(Res.string.order_payment, order.paymentMethod.label),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.quantity}× ${item.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(FormatHelper.euro(item.subtotal), style = MaterialTheme.typography.bodyMedium)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(Res.string.order_total),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = FormatHelper.euro(order.totalPrice),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderSummaryCardPreview() {
    TableoTheme {
        OrderSummaryCard(order = Order(
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
