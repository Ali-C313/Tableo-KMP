package com.example.tableo.util

import com.example.tableo.data.model.Coordinates
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Grobe Durchschnittsgeschwindigkeit in der Stadt, für die geschätzte Ankunft. */
private const val AverageSpeedKmh = 25.0

/** Luftlinie zwischen zwei Punkten in Kilometern (Haversine-Formel). */
fun distanceInKm(from: Coordinates, to: Coordinates): Double {
    val earthRadiusKm = 6371.0
    val dLat = (to.latitude - from.latitude).toRadians()
    val dLng = (to.longitude - from.longitude).toRadians()
    val a = sin(dLat / 2).pow(2) +
            cos(from.latitude.toRadians()) * cos(to.latitude.toRadians()) * sin(dLng / 2).pow(2)
    return 2 * earthRadiusKm * asin(sqrt(a))
}

/** Geschätzte Fahrzeit in Minuten, mindestens 1. */
fun estimatedMinutes(distanceKm: Double): Int =
    ceil(distanceKm / AverageSpeedKmh * 60).toInt().coerceAtLeast(1)

/** z.B. "1,4 km" oder "350 m" */
fun formatDistance(distanceKm: Double): String =
    if (distanceKm < 1) "${(distanceKm * 1000).toInt()} m"
    else "${distanceKm.formatDecimals(1)} km"

/** Grobe Zeit für die Übergabe an der Haustür. */
private const val MinutesPerStop = 3

/**
 * Fahrzeit vom Fahrer über alle Lieferungen vor dem Kunden bis zum Kunden.
 * Pro Zwischenstopp kommen ein paar Minuten für die Übergabe dazu.
 *
 * @param stopsBefore Adressen, die der Fahrer vorher noch beliefert, in Tour-Reihenfolge
 */
fun estimatedMinutesViaStops(driver: Coordinates, stopsBefore: List<Coordinates>, destination: Coordinates): Int {
    var totalKm = 0.0
    var from = driver
    (stopsBefore + destination).forEach { stop ->
        totalKm += distanceInKm(from, stop)
        from = stop
    }
    return estimatedMinutes(totalKm) + stopsBefore.size * MinutesPerStop
}

private fun Double.toRadians(): Double = this * PI / 180
