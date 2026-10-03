package com.example.tableo.ui.util

import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.Order

/** Feste Standard-Zubereitungszeit, bis es echte Zeitangaben vom Restaurant gibt. */
private const val DefaultPreparationMinutes = 20

/** Grobe Fahrzeit bei Lieferung, kommt noch auf die Zubereitung drauf. */
private const val DefaultDeliveryMinutes = 20

/**
 * Geschätzter Zeitpunkt, zu dem das Essen da ist, als Text. Bei Abholung die gewählte Uhrzeit,
 * bei Lieferung Zubereitung plus Fahrzeit, sonst nur die Zubereitung.
 */
fun Order.estimatedReadyTimeText(): String {
    if (orderType == OrderType.PICKUP && !pickupTime.isNullOrBlank()) return "$pickupTime Uhr"
    val minutes = if (orderType == OrderType.DELIVERY) {
        DefaultPreparationMinutes + DefaultDeliveryMinutes
    } else {
        DefaultPreparationMinutes
    }
    val readyAt = createdAt + minutes * 60_000L
    return "ca. ${formatDateTime(readyAt, "HH:mm")} Uhr"
}

/** Kurzer Zusatztext unter der geschätzten Zeit. */
fun Order.estimatedHint(): String = when (orderType) {
    OrderType.PICKUP -> "Zu deiner gewählten Abholzeit"
    OrderType.DELIVERY -> "Du kannst den Fahrer live verfolgen, sobald er losfährt"
    OrderType.DINE_IN -> "In etwa $DefaultPreparationMinutes Minuten an deinem Tisch"
}

/** Bestellart als kurzer Text, z.B. "Tisch 7", "Abholung um 19:30 Uhr" oder "Lieferung". */
fun Order.orderTypeLabel(): String = when (orderType) {
    OrderType.DINE_IN -> "Tisch $tableNumber"
    OrderType.PICKUP -> "Abholung um $pickupTime Uhr"
    OrderType.DELIVERY -> "Lieferung"
}

/** Kurze Bestellnummer zum Vorlesen, z.B. "#A1B2C3". Kommt aus der Firestore-ID. */
fun Order.orderNumber(): String = "#" + id.take(6).uppercase()
