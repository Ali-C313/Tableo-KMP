package com.example.tableo.data.enums

/** Auswählbare Zeiträume fürs Statistik-Diagramm. */
enum class AnalyticsPeriod(val label: String, val millis: Long, val bucketCount: Int) {
    LAST_24H("24 Stunden", 24L * 60 * 60 * 1000, 6),
    LAST_3D("3 Tage", 3L * 24 * 60 * 60 * 1000, 3),
    LAST_MONTH("1 Monat", 30L * 24 * 60 * 60 * 1000, 6),
    LAST_6_MONTHS("6 Monate", 182L * 24 * 60 * 60 * 1000, 6)
}