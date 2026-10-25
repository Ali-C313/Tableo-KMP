package com.example.tableo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.tableo.data.enums.label
import com.example.tableo.resources.Res
import com.example.tableo.resources.common_guest
import com.example.tableo.resources.component_staff_waiting_for_driver
import com.example.tableo.resources.component_staff_driver_picking_up
import com.example.tableo.resources.component_staff_driver_on_the_way
import com.example.tableo.resources.component_staff_start_preparing
import com.example.tableo.resources.component_staff_finish_preparing
import com.example.tableo.resources.component_staff_done
import com.example.tableo.resources.component_staff_done_to_driver
import com.example.tableo.resources.component_staff_cancel_order
import com.example.tableo.resources.component_staff_cancel_text
import com.example.tableo.util.FormatHelper
import com.example.tableo.util.OrderHelper
import org.jetbrains.compose.resources.stringResource

/**
 * Eine Bestellung im Personal-Dashboard: Kunde, Bestellart, Positionen, Bezahlart, Summe, Status-Aktionen.
 * Liegt eine Lieferung schon beim Fahrer, gibt es keine Buttons mehr, nur den Stand.
 *
 * @param onAdvanceStatus Wird aufgerufen, um die Bestellung einen Schritt weiterzuschalten
 * @param onMarkAsDone Erledigt: ins Archiv, bei Lieferung an die Fahrer
 * @param onCancel Storniert die Bestellung, vorher kommt eine Sicherheitsabfrage
 */
@Composable
fun OrderCard(
    order: Order,
    onAdvanceStatus: () -> Unit,
    onMarkAsDone: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    val guest = stringResource(Res.string.common_guest)

    val itemsText = order.items.joinToString(", ") { "${it.quantity}× ${it.name}" }
    val canAdvance = order.status == OrderStatus.OFFEN || order.status == OrderStatus.IN_ZUBEREITUNG
    val isDelivery = order.orderType == OrderType.DELIVERY
    val driverText = when (order.status) {
        OrderStatus.ABHOLBEREIT -> stringResource(Res.string.component_staff_waiting_for_driver)
        OrderStatus.ANGENOMMEN -> stringResource(Res.string.component_staff_driver_picking_up, order.driverName.orEmpty())
        OrderStatus.UNTERWEGS -> stringResource(Res.string.component_staff_driver_on_the_way, order.driverName.orEmpty())
        else -> null
    }

    Card(
        modifier = modifier,
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Card)
    ) {
        Column(modifier = Modifier.padding(AppSpacing.M)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    OrderHelper.orderTypeLabel(order),
                    style = MaterialTheme.typography.titleMedium
                )
                StatusPill(order.status)
            }
            Text(
                text = "${OrderHelper.orderNumber(order)} · ${order.customerName.ifBlank { guest }} · ${FormatHelper.dateTime(order.createdAt)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
            if (isDelivery && !order.deliveryAddress.isNullOrBlank()) {
                Text(
                    text = order.deliveryAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
                )
            }
            Text(
                text = itemsText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.XS + 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = FormatHelper.euro(order.totalPrice),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = order.paymentMethod.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Lieferung liegt beim Fahrer, das Personal sieht nur noch den Stand
            if (driverText != null) {
                Text(
                    driverText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = AppSpacing.S)
                )
            } else {
                Row(
                    modifier = Modifier.padding(top = AppSpacing.S + AppSpacing.XS),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.S + 2.dp)
                ) {
                    if (canAdvance) {
                        Button(
                            onClick = onAdvanceStatus,
                            shape = AppShape.Small,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (order.status == OrderStatus.OFFEN) stringResource(Res.string.component_staff_start_preparing)
                                else stringResource(Res.string.component_staff_finish_preparing)
                            )
                        }
                        OutlinedButton(
                            onClick = onMarkAsDone,
                            shape = AppShape.Small,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(Res.string.component_staff_done))
                        }
                    } else {
                        // Bestellung ist fertig, jetzt nur noch abschließen
                        Button(
                            onClick = onMarkAsDone,
                            shape = AppShape.Small,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (isDelivery) stringResource(Res.string.component_staff_done_to_driver)
                                else stringResource(Res.string.component_staff_done)
                            )
                        }
                    }
                }
            }

            // geht nur, bis die Bestellung das Restaurant verlassen hat
            if (order.status.canRestaurantCancel) {
                TextButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(Res.string.component_staff_cancel_order), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showCancelDialog) {
        CancelOrderDialog(
            text = stringResource(Res.string.component_staff_cancel_text, order.customerName.ifBlank { guest }),
            onConfirm = onCancel,
            onDismiss = { showCancelDialog = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderCardPreview() {
    TableoTheme {
        OrderCard(
            order = Order(
                id = "1",
                restaurantId = "layali-lounge",
                orderType = OrderType.DINE_IN,
                tableNumber = 7,
                items = listOf(
                    OrderItem(
                        menuItemId = "1",
                        name = "Doppelapfel",
                        pricePerItem = 12.0,
                        quantity = 2
                    )
                ),
                status = OrderStatus.IN_ZUBEREITUNG
            ),
            onAdvanceStatus = {},
            onMarkAsDone = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderCardDeliveryPreview() {
    TableoTheme {
        OrderCard(
            order = Order(
                id = "2",
                restaurantId = "layali-lounge",
                orderType = OrderType.DELIVERY,
                customerName = "Sara",
                deliveryAddress = "Schanzenstraße 5, 20357 Hamburg",
                paymentMethod = PaymentMethod.PAYPAL,
                items = listOf(OrderItem(menuItemId = "2", name = "Falafel-Teller", pricePerItem = 11.5, quantity = 1)),
                status = OrderStatus.FERTIG
            ),
            onAdvanceStatus = {},
            onMarkAsDone = {},
            onCancel = {}
        )
    }
}
