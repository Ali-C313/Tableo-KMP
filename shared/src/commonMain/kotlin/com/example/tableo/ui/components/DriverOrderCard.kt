package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.enums.PaymentMethod
import com.example.tableo.data.model.Order
import com.example.tableo.data.model.OrderItem
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing
import com.example.tableo.ui.util.asEuro

/**
 * Eine Lieferung im Fahrer-Bereich: Kunde, Adresse, Artikel und Bezahlart.
 * Die Buttons kommen von außen, weil sie in "Abholbereit" und "Meine Tour" verschieden sind.
 *
 * @param position Platz in der Tour, wird als Nummer links angezeigt. null = keine Nummer
 * @param actions Buttons unten in der Karte
 */
@Composable
fun DriverOrderCard(
    order: Order,
    modifier: Modifier = Modifier,
    position: Int? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Card)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.M)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (position != null) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$position",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (position != null) 10.dp else 0.dp)
                ) {
                    Text(
                        order.customerName.ifBlank { "Gast" },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        order.deliveryAddress ?: "Keine Adresse",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusPill(order.status, modifier = Modifier.padding(start = 8.dp))
            }
            Text(
                text = order.items.joinToString(", ") { "${it.quantity}× ${it.name}" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
            )
            // wichtig für den Fahrer: muss er noch Geld kassieren?
            Text(
                text = "${order.totalPrice.asEuro()} · ${order.paymentMethod.label}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.S + AppSpacing.XS),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DriverOrderCardPreview() {
    TableoTheme {
        DriverOrderCard(
            order = Order(
                id = "3",
                customerName = "Sara",
                orderType = OrderType.DELIVERY,
                deliveryAddress = "Schanzenstraße 5, 20357 Hamburg",
                paymentMethod = PaymentMethod.BAR,
                items = listOf(OrderItem(menuItemId = "3", name = "Falafel-Teller", pricePerItem = 11.5, quantity = 2)),
                status = OrderStatus.UNTERWEGS
            ),
            position = 1,
            actions = {
                Button(onClick = {}, shape = AppShape.Small, modifier = Modifier.weight(1f)) { Text("Zugestellt") }
            }
        )
    }
}
