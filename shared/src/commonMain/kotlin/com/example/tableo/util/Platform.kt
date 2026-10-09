package com.example.tableo.util

/** Aktuelle Zeit in Millisekunden seit 1970, auf Android und iOS gleich. */
expect fun currentTimeMillis(): Long

/**
 * Formatiert einen Zeitstempel in der Zeitzone des Handys.
 *
 * @param pattern z.B. "dd.MM.yyyy, HH:mm" oder "HH:mm"
 */
expect fun formatDateTime(millis: Long, pattern: String): String

/** Minuten seit Mitternacht in der Zeitzone des Handys, z.B. 14:30 -> 870. */
expect fun currentMinuteOfDay(): Int
