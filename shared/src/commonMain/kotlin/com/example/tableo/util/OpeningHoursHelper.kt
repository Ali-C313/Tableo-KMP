package com.example.tableo.util

/** Öffnungszeiten der Restaurants prüfen. */
object OpeningHoursHelper {

    /** Ob das Restaurant gerade geöffnet hat, mit der aktuellen Uhrzeit vom Handy. */
    fun isOpenNow(openingTime: String, closingTime: String): Boolean =
        isOpenAt(currentMinuteOfDay(), openingTime, closingTime)

    /**
     * Ob das Restaurant zu einer bestimmten Uhrzeit geöffnet hat.
     * Klappt auch über Mitternacht, z.B. 18:00 - 02:00.
     *
     * @param minuteOfDay Minuten seit Mitternacht, z.B. 14:30 -> 870
     */
    fun isOpenAt(minuteOfDay: Int, openingTime: String, closingTime: String): Boolean {
        return try {
            val openMinutes = toMinutes(openingTime)
            val closeMinutes = toMinutes(closingTime)

            if (closeMinutes > openMinutes) {
                minuteOfDay in openMinutes until closeMinutes
            } else {
                minuteOfDay >= openMinutes || minuteOfDay < closeMinutes
            }
        } catch (_: Exception) {
            // ungültiges Format -> lieber als geöffnet anzeigen statt fälschlich zu sperren
            true
        }
    }

    /** "HH:mm" in Minuten seit Mitternacht, z.B. "14:30" -> 870. */
    private fun toMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].trim().toInt()
        val minutes = parts[1].trim().toInt()
        return hours * 60 + minutes
    }
}
