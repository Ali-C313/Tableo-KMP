package com.example.tableo.data.model

import kotlinx.serialization.Serializable

/**
 * Eine Position innerhalb einer Bestellung.
 * Name und Preis werden beim Bestellen aus dem [MenuItem] übernommen (Snapshot),
 * damit spätere Preisänderungen bestehende Bestellungen nicht verändern.
 *
 * @param menuItemId ID des zugehörigen [MenuItem]
 * @param name Name zum Zeitpunkt der Bestellung
 * @param pricePerItem Preis pro Stück zum Zeitpunkt der Bestellung
 * @param quantity Bestellte Menge
 */
@Serializable
data class OrderItem(
    val menuItemId: String = "",
    val name: String = "",
    val pricePerItem: Double = 0.0,
    val quantity: Int = 0
) {
    /** Zwischensumme dieser Position (Preis * Menge). */
    val subtotal: Double
        get() = pricePerItem * quantity
}