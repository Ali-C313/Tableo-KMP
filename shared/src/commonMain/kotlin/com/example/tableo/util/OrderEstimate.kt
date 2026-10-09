package com.example.tableo.util

import androidx.compose.runtime.Composable
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.Order
import com.example.tableo.resources.Res
import com.example.tableo.resources.estimate_hint_delivery
import com.example.tableo.resources.estimate_hint_pickup
import com.example.tableo.resources.estimate_hint_table
import com.example.tableo.resources.order_type_delivery
import com.example.tableo.resources.order_type_pickup_at
import com.example.tableo.resources.order_type_table
import com.example.tableo.resources.time_approx_oclock
import com.example.tableo.resources.time_oclock
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/** Feste Standard-Zubereitungszeit, bis es echte Zeitangaben vom Restaurant gibt. */
private const val DefaultPreparationMinutes = 20

/** Grobe Fahrzeit bei Lieferung, kommt noch auf die Zubereitung drauf. */
private const val DefaultDeliveryMinutes = 20

/**
 * Geschätzter Zeitpunkt, zu dem das Essen da ist, als Text. Bei Abholung die gewählte Uhrzeit,
 * bei Lieferung Zubereitung plus Fahrzeit, sonst nur die Zubereitung.
 */
@Composable
fun Order.estimatedReadyTimeText(): String {
    if (orderType == OrderType.PICKUP && !pickupTime.isNullOrBlank()) {
        return stringResource(Res.string.time_oclock, pickupTime)
    }
    val minutes = if (orderType == OrderType.DELIVERY) {
        DefaultPreparationMinutes + DefaultDeliveryMinutes
    } else {
        DefaultPreparationMinutes
    }
    val readyAt = createdAt + minutes * 60_000L
    return stringResource(Res.string.time_approx_oclock, formatDateTime(readyAt, "HH:mm"))
}

/** Kurzer Zusatztext unter der geschätzten Zeit. */
@Composable
fun Order.estimatedHint(): String = when (orderType) {
    OrderType.PICKUP -> stringResource(Res.string.estimate_hint_pickup)
    OrderType.DELIVERY -> stringResource(Res.string.estimate_hint_delivery)
    OrderType.DINE_IN -> stringResource(Res.string.estimate_hint_table, DefaultPreparationMinutes)
}

/** Bestellart als kurzer Text, z.B. "Tisch 7", "Abholung um 19:30 Uhr" oder "Lieferung". */
@Composable
fun Order.orderTypeLabel(): String = when (orderType) {
    OrderType.DINE_IN -> stringResource(Res.string.order_type_table, tableNumber.toString())
    OrderType.PICKUP -> stringResource(Res.string.order_type_pickup_at, pickupTime.orEmpty())
    OrderType.DELIVERY -> stringResource(Res.string.order_type_delivery)
}

/** Wie [orderTypeLabel], aber außerhalb von Compose, z.B. für Benachrichtigungen. */
suspend fun Order.orderTypeText(): String = when (orderType) {
    OrderType.DINE_IN -> getString(Res.string.order_type_table, tableNumber.toString())
    OrderType.PICKUP -> getString(Res.string.order_type_pickup_at, pickupTime.orEmpty())
    OrderType.DELIVERY -> getString(Res.string.order_type_delivery)
}

/** Kurze Bestellnummer zum Vorlesen, z.B. "#A1B2C3". Kommt aus der Firestore-ID. */
fun Order.orderNumber(): String = "#" + id.take(6).uppercase()
