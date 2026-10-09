package com.example.tableo.util

/**
 * Prüft, ob ein Restaurant anhand seiner Öffnungszeiten gerade geöffnet hat.
 * Unterstützt auch Zeiträume über Mitternacht (z.B. 18:00 - 02:00).
 * Rechnet nur mit Minuten, die aktuelle Uhrzeit kommt je nach Plattform aus [currentMinuteOfDay].
 */
fun isRestaurantOpenNow(openingTime: String, closingTime: String): Boolean {
    return try {
        val nowMinutes = currentMinuteOfDay()
        val openMinutes = timeStringToMinutes(openingTime)
        val closeMinutes = timeStringToMinutes(closingTime)

        if (closeMinutes > openMinutes) {
            nowMinutes in openMinutes until closeMinutes
        } else {
            nowMinutes >= openMinutes || nowMinutes < closeMinutes
        }
    } catch (_: Exception) {
        // ungültiges Zeitformat -> lieber als geöffnet anzeigen statt fälschlich zu sperren
        true
    }
}

/** Wandelt "HH:mm" in die Anzahl Minuten seit Mitternacht um, z.B. "14:30" -> 870. */
private fun timeStringToMinutes(time: String): Int {
    val parts = time.split(":")
    val hours = parts[0].trim().toInt()
    val minutes = parts[1].trim().toInt()
    return hours * 60 + minutes
}