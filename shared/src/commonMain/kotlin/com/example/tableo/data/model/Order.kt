package com.example.tableo.data.model

import com.example.tableo.util.currentTimeMillis
import kotlinx.serialization.Serializable

import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.enums.PaymentMethod

/**
 * Eine Bestellung für ein Restaurant, am Tisch, zum Abholen oder als Lieferung.
 *
 * @param id Eindeutige ID, gleich der Firestore-Dokument-ID (wird beim Laden gesetzt)
 * @param restaurantId ID des Restaurants, zu dem die Bestellung gehört
 * @param restaurantName Name des Restaurants zum Zeitpunkt der Bestellung (Snapshot, für Anzeige in der Bestellhistorie)
 * @param userId ID des Kunden, falls er eingeloggt war; null bei anonymer Bestellung
 * @param customerName Name des Kunden beim Bestellen, "Gast" wenn er nicht angemeldet war
 * @param orderType Am Tisch, Abholung oder Lieferung
 * @param tableNumber Tischnummer, nur gesetzt wenn [orderType] == DINE_IN
 * @param pickupTime Gewünschte Abholzeit, nur gesetzt wenn [orderType] == PICKUP
 * @param deliveryAddress Lieferadresse als Text, nur bei DELIVERY
 * @param deliveryLocation Position der Lieferadresse, nur bei DELIVERY
 * @param driverLocation Letzter bekannter Standort des Fahrers, wird während der Fahrt laufend aktualisiert
 * @param driverName Name des Fahrers, der die Lieferung angenommen hat
 * @param tourPosition Platz in der Tour des Fahrers, 1 = wird als Nächstes geliefert
 * @param paymentMethod Vom Gast gewählte Bezahlart
 * @param items Liste der bestellten Positionen
 * @param status Aktueller Bearbeitungsstatus, siehe [OrderStatus]
 * @param cancelledByRestaurant true, wenn das Personal storniert hat, false wenn der Kunde
 * @param createdAt Zeitstempel der Bestellung (Millisekunden seit Epoch)
 */
@Serializable
data class Order(
    val id: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val userId: String? = null,
    val customerName: String = "",
    val orderType: OrderType = OrderType.DINE_IN,
    val tableNumber: Int? = null,
    val pickupTime: String? = null,
    val deliveryAddress: String? = null,
    val deliveryLocation: Coordinates? = null,
    val driverLocation: Coordinates? = null,
    val driverName: String? = null,
    val tourPosition: Int? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.BAR,
    val items: List<OrderItem> = emptyList(),
    val status: OrderStatus = OrderStatus.OFFEN,
    val cancelledByRestaurant: Boolean = false,
    val createdAt: Long = currentTimeMillis()
) {
    /** Gesamtsumme der Bestellung über alle Positionen. */
    val totalPrice: Double
        get() = items.sumOf { it.subtotal }
}
