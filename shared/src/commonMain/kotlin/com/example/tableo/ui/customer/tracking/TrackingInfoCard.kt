package com.example.tableo.ui.customer.tracking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.Order
import com.example.tableo.ui.components.StatusPill
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.resources.Res
import com.example.tableo.resources.tracking_delivered
import com.example.tableo.resources.tracking_driver_fallback
import com.example.tableo.resources.tracking_headline_cancelled
import com.example.tableo.resources.tracking_headline_delivered
import com.example.tableo.resources.tracking_headline_driver_has_order
import com.example.tableo.resources.tracking_headline_eta
import com.example.tableo.resources.tracking_headline_preparing
import com.example.tableo.resources.tracking_headline_waiting_driver
import com.example.tableo.resources.tracking_sub_on_the_way
import com.example.tableo.resources.tracking_sub_on_the_way_distance
import com.example.tableo.resources.tracking_sub_picking_up
import com.example.tableo.resources.tracking_sub_stops_before
import com.example.tableo.util.DistanceHelper
import com.example.tableo.util.FormatHelper
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.data.model.OrderItem
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.enums.PaymentMethod

/** Status, geschätzte Ankunft und wie viele Lieferungen der Fahrer vorher noch hat. */
@Composable
fun TrackingInfoCard(order: Order, stopsBefore: List<Coordinates>) {
    val driverLocation = order.driverLocation
    val destination = order.deliveryLocation
    // aus Datenschutzgründen nur der Vorname
    val driverName = order.driverName?.let { FormatHelper.firstName(it) } ?: stringResource(Res.string.tracking_driver_fallback)

    val minutes = if (driverLocation != null && destination != null) {
        DistanceHelper.estimatedMinutesViaStops(driverLocation, stopsBefore, destination)
    } else null
    val directKm = if (driverLocation != null && destination != null) DistanceHelper.distanceInKm(driverLocation, destination) else null

    val headline = when (order.status) {
        OrderStatus.OFFEN, OrderStatus.IN_ZUBEREITUNG -> stringResource(Res.string.tracking_headline_preparing)
        OrderStatus.FERTIG, OrderStatus.ABHOLBEREIT -> stringResource(Res.string.tracking_headline_waiting_driver)
        OrderStatus.ANGENOMMEN, OrderStatus.UNTERWEGS ->
            minutes?.let { stringResource(Res.string.tracking_headline_eta, it) }
                ?: stringResource(Res.string.tracking_headline_driver_has_order, driverName)
        OrderStatus.ERLEDIGT, OrderStatus.BEZAHLT -> stringResource(Res.string.tracking_headline_delivered)
        OrderStatus.STORNIERT -> stringResource(Res.string.tracking_headline_cancelled)
    }

    val subline = when (order.status) {
        OrderStatus.ANGENOMMEN -> stringResource(Res.string.tracking_sub_picking_up, driverName)
        OrderStatus.UNTERWEGS -> when {
            stopsBefore.isNotEmpty() ->
                stringResource(Res.string.tracking_sub_stops_before, driverName, stopsBefore.size)
            directKm != null -> stringResource(Res.string.tracking_sub_on_the_way_distance, driverName, DistanceHelper.formatDistance(directKm))
            else -> stringResource(Res.string.tracking_sub_on_the_way, driverName)
        }
        else -> null
    }

    Card(
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            StatusPill(order.status, doneLabel = stringResource(Res.string.tracking_delivered))
            Text(
                headline,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 10.dp)
            )
            subline?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                "${order.restaurantName} · ${order.deliveryAddress.orEmpty()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackingInfoCardPreview() {
    TableoTheme {
        TrackingInfoCard(order = Order(
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
        ), stopsBefore = emptyList())
    }
}
