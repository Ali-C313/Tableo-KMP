package com.example.tableo.ui.customer.orderDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.data.model.Order
import com.example.tableo.ui.components.StatusPill
import com.example.tableo.util.asDateTime
import com.example.tableo.util.orderNumber
import com.example.tableo.resources.Res
import com.example.tableo.resources.order_detail_order_id
import com.example.tableo.resources.order_detail_order_number
import com.example.tableo.resources.order_detail_ordered_at
import com.example.tableo.resources.status_completed
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.data.model.OrderItem
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.PaymentMethod

/** Bestellnummer, Status, Restaurant und Zeitpunkt. Die volle ID lässt sich markieren und kopieren. */
@Composable
fun HeaderCard(order: Order) {
    DetailCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(Res.string.order_detail_order_number, order.orderNumber()), style = MaterialTheme.typography.titleLarge)
            StatusPill(order.status, doneLabel = stringResource(Res.string.status_completed))
        }
        Text(
            order.restaurantName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            stringResource(Res.string.order_detail_ordered_at, order.createdAt.asDateTime()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // lange gedrückt halten zum Kopieren, z.B. für Rückfragen beim Restaurant
        SelectionContainer {
            Text(
                stringResource(Res.string.order_detail_order_id, order.id),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HeaderCardPreview() {
    TableoTheme {
        HeaderCard(order = Order(
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
