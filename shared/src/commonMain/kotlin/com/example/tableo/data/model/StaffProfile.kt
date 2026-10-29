package com.example.tableo.data.model

import com.example.tableo.data.enums.StaffRole
import kotlinx.serialization.Serializable

/**
 * Ein Mitarbeiter eines Restaurants. Entsteht beim Registrieren mit Restaurant-ID
 * oder beim nachträglichen Verknüpfen im Konto.
 *
 * @param id Firestore-Dokument-ID, gleich der [userId]
 * @param restaurantId Restaurant, zu dem der Mitarbeiter gehört
 * @param userId Firebase-Auth-UID des zugehörigen Kontos
 * @param name Anzeigename im Team
 * @param email E-Mail des Kontos, für die Detailansicht im Team
 * @param photoUrl Profilbild des Kontos, leer wenn keins hochgeladen ist
 * @param role Aufgabe im Restaurant, vergibt das Team im Team-Reiter
 * @param joinedAt Zeitpunkt, seit dem er im Team ist (Millisekunden), 0 bei alten Einträgen
 * @param onDuty true, solange er im Dienst ist und Nachrichten zu neuen Bestellungen bekommt
 */
@Serializable
data class StaffProfile(
    val id: String = "",
    val restaurantId: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val role: StaffRole = StaffRole.OHNE,
    val joinedAt: Long = 0,
    val onDuty: Boolean = false
)
