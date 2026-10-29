package com.example.tableo.data.model

import kotlinx.serialization.Serializable

/**
 * Ein Eintrag in der Speisekarte eines Restaurants.
 *
 * @param id Eindeutige ID, gleich der Firestore-Dokument-ID (wird beim Laden gesetzt)
 * @param restaurantId ID des Restaurants, zu dem dieser Eintrag gehört
 * @param name Name des Gerichts/Getränks
 * @param description Kurzbeschreibung, z.B. "Klassiker, mittelstark"
 * @param price Preis in Euro
 * @param category Freier Text statt festem Enum, z.B. "Hauptspeisen", "Getränke", "Shisha".
 * Jedes Restaurant definiert seine eigenen Kategorien direkt in Firestore.
 * @param imageUrl Optionale Foto-URL; leer bedeutet "Kein Bild vorhanden"
 * @param available Ob der Artikel aktuell bestellbar ist
 */
@Serializable
data class MenuItem(
    val id: String = "",
    val restaurantId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val available: Boolean = true
)