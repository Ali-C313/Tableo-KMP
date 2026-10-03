package com.example.tableo.data.repository

/** Zeigt Benachrichtigungen auf dem Handy an, z.B. "Deine Bestellung ist unterwegs". */
interface NotificationRepository {

    /**
     * Zeigt eine Benachrichtigung. Fehlt die Berechtigung, passiert einfach nichts.
     *
     * @param id Gleiche ID ersetzt eine alte Benachrichtigung, z.B. die Bestell-ID als Hash
     */
    fun show(id: Int, title: String, text: String)
}
