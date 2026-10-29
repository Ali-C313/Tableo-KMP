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
import com.example.tableo.data.model.Order
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing
import com.example.tableo.resources.Res
import com.example.tableo.resources.component_orders_cancel_hint
import com.example.tableo.resources.component_orders_cancel_order
import com.example.tableo.resources.component_orders_reorder
import com.example.tableo.resources.component_orders_track_live
import com.example.tableo.resources.status_completed
import com.example.tableo.util.FormatHelper
import com.example.tableo.util.OrderHelper
import org.jetbrains.compose.resources.stringResource

/**
 * Eine Bestellung in der Bestellhistorie des Kunden ("Meine Bestellungen").
 *
 * @param onReorderClick Übernimmt die Artikel dieser Bestellung erneut in den Warenkorb
 * @param onTrackClick Öffnet das Live-Tracking, nur bei laufenden Lieferungen sichtbar
 * @param onCancelClick Storniert die Bestellung, nur solange das Restaurant noch nicht angefangen hat
 * @param onClick Öffnet die Detailansicht der Bestellung
 */
@Composable
fun MyOrderCard(
    order: Order,
    onReorderClick: () -> Unit = {},
    onTrackClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val orderTypeText = OrderHelper.orderTypeLabel(order)
    val canTrack = order.orderType == OrderType.DELIVERY && !order.status.isDone
    val canCancel = order.status == OrderStatus.OFFEN
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.M)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    order.restaurantName,
                    style = MaterialTheme.typography.titleMedium
                )
                StatusPill(order.status, doneLabel = stringResource(Res.string.status_completed))
            }
            Text(
                text = "${OrderHelper.orderNumber(order)} · ${FormatHelper.dateTime(order.createdAt)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = orderTypeText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
            )
            Text(
                text = OrderHelper.itemsText(order),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
            )
            Text(
                text = FormatHelper.euro(order.totalPrice),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
            )
            if (canTrack) {
                Button(
                    onClick = onTrackClick,
                    shape = AppShape.Small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppSpacing.S + AppSpacing.XS)
                ) { Text(stringResource(Res.string.component_orders_track_live)) }
            }
            OutlinedButton(
                onClick = onReorderClick,
                shape = AppShape.Small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.S + AppSpacing.XS)
            ) { Text(stringResource(Res.string.component_orders_reorder)) }
            if (canCancel) {
                TextButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.component_orders_cancel_order), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showCancelDialog) {
        CancelOrderDialog(
            text = stringResource(Res.string.component_orders_cancel_hint, order.restaurantName),
            onConfirm = onCancelClick,
            onDismiss = { showCancelDialog = false }
        )
    }
}

@Preview
@Composable
private fun MyOrderCardPreview() {
    MyOrderCard(order = Order())
}