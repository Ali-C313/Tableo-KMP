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
import com.example.tableo.resources.Res
import com.example.tableo.resources.status_cancelled
import com.example.tableo.resources.status_done
import com.example.tableo.resources.status_new
import com.example.tableo.resources.status_on_the_way
import com.example.tableo.resources.status_preparing
import com.example.tableo.resources.status_ready
import com.example.tableo.resources.status_ready_for_pickup
import com.example.tableo.resources.status_with_driver
import org.jetbrains.compose.resources.stringResource
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
    doneLabel: String = stringResource(Res.string.status_done),
    modifier: Modifier = Modifier
) {
    val (label, color) = when (status) {
        OrderStatus.OFFEN -> stringResource(Res.string.status_new) to MaterialTheme.colorScheme.primary
        OrderStatus.IN_ZUBEREITUNG -> stringResource(Res.string.status_preparing) to MaterialTheme.colorScheme.primary
        OrderStatus.FERTIG -> stringResource(Res.string.status_ready) to MaterialTheme.colorScheme.secondary
        OrderStatus.ABHOLBEREIT -> stringResource(Res.string.status_ready_for_pickup) to MaterialTheme.colorScheme.tertiary
        OrderStatus.ANGENOMMEN -> stringResource(Res.string.status_with_driver) to MaterialTheme.colorScheme.tertiary
        OrderStatus.UNTERWEGS -> stringResource(Res.string.status_on_the_way) to MaterialTheme.colorScheme.tertiary
        OrderStatus.ERLEDIGT, OrderStatus.BEZAHLT -> doneLabel to MaterialTheme.colorScheme.secondary
        OrderStatus.STORNIERT -> stringResource(Res.string.status_cancelled) to MaterialTheme.colorScheme.error
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