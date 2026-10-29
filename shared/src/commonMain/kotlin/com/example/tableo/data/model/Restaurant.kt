package com.example.tableo.data.model

import kotlinx.serialization.Serializable

/**
 * Ein Restaurant/eine Shisha-Bar, die an der App teilnimmt.
 *
 * @param id Eindeutige ID, gleich der Firestore-Dokument-ID (wird beim Laden gesetzt)
 * @param name Anzeigename, z.B. "Layali Lounge"
 * @param type Art der Location, z.B. "Restaurant", "Shisha-Bar", "Café", für den Filter in der Übersicht
 * @param address Adresse als ein Text, z.B. "Reeperbahn 12, 20359 Hamburg"
 * @param imageUrl Bild/Logo-URL, kann leer sein
 * @param cuisineTypes Was das Restaurant anbietet, z.B. ["Burger", "Pizza"], für die Kategorie-Kacheln oben
 * @param openingTime Öffnungszeit im Format "HH:mm", z.B. "12:00"
 * @param closingTime Schließzeit im Format "HH:mm", z.B. "23:00"
 * @param offersDelivery false = nur Abholung und vor Ort, taucht bei "Lieferung" nicht auf
 */
@Serializable
data class Restaurant(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val address: String = "",
    val cuisineTypes: List<String> = emptyList(),
    val imageUrl: String = "",
    val openingTime: String = "00:00",
    val closingTime: String = "23:59",
    val offersDelivery: Boolean = true
)