package com.example.tableo.data.model

/**
 * Lieferadresse des Kunden, entweder eingetippt oder über den Standort ermittelt.
 *
 * @param text Adresse als lesbarer Text, z.B. "Reeperbahn 12, 20359 Hamburg"
 * @param coordinates Position der Adresse, für die Karte beim Tracking
 */
data class DeliveryAddress(
    val text: String = "",
    val coordinates: Coordinates = Coordinates()
)
