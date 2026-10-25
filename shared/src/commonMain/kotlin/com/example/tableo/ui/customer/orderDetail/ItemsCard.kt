package com.example.tableo.ui.customer.orderDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.data.model.Order
import com.example.tableo.resources.Res
import com.example.tableo.resources.order_detail_price_each
import com.example.tableo.resources.order_detail_your_order
import com.example.tableo.resources.order_total
import com.example.tableo.util.FormatHelper
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.data.model.OrderItem
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.PaymentMethod

/** Alle Positionen mit Menge, Einzelpreis und Zwischensumme, darunter die Gesamtsumme. */
@Composable
fun ItemsCard(order: Order) {
    DetailCard(modifier = Modifier.padding(top = 12.dp)) {
        Text(stringResource(Res.string.order_detail_your_order), style = MaterialTheme.typography.titleMedium)
        order.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${item.quantity}× ${item.name}", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        stringResource(Res.string.order_detail_price_each, FormatHelper.euro(item.pricePerItem)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(FormatHelper.euro(item.subtotal), style = MaterialTheme.typography.bodyLarge)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(Res.string.order_total), style = MaterialTheme.typography.titleMedium)
            Text(
                FormatHelper.euro(order.totalPrice),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemsCardPreview() {
    TableoTheme {
        ItemsCard(order = Order(
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
