package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppShape

/**
 * Farbige Status-Kapsel einer Bestellung, z.B. Neu, In Zubereitung, Unterwegs oder Erledigt.
 *
 * @param doneLabel Text für abgeschlossene Bestellungen: beim Personal "Erledigt", beim Kunden "Abgeschlossen"
 */
@Composable
fun StatusPill(
    status: OrderStatus,
    doneLabel: String = "Erledigt",
    modifier: Modifier = Modifier
) {
    val (label, color) = when (status) {
        OrderStatus.OFFEN -> "Neu" to MaterialTheme.colorScheme.primary
        OrderStatus.IN_ZUBEREITUNG -> "In Zubereitung" to MaterialTheme.colorScheme.primary
        OrderStatus.FERTIG -> "Fertig" to MaterialTheme.colorScheme.secondary
        OrderStatus.ABHOLBEREIT -> "Abholbereit" to MaterialTheme.colorScheme.tertiary
        OrderStatus.ANGENOMMEN -> "Beim Fahrer" to MaterialTheme.colorScheme.tertiary
        OrderStatus.UNTERWEGS -> "Unterwegs" to MaterialTheme.colorScheme.tertiary
        OrderStatus.ERLEDIGT, OrderStatus.BEZAHLT -> doneLabel to MaterialTheme.colorScheme.secondary
        OrderStatus.STORNIERT -> "Storniert" to MaterialTheme.colorScheme.error
    }
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.16f), AppShape.Large)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusPillPreview() {
    TableoTheme {
        StatusPill(
            status = OrderStatus.IN_ZUBEREITUNG
        )
    }
}