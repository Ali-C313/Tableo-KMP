package com.example.tableo.util

import com.example.tableo.data.model.Coordinates
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Entfernungen und Fahrzeiten für die Lieferung. */
object DistanceHelper {

    /** Grobe Durchschnittsgeschwindigkeit in der Stadt. */
    private const val AVERAGE_SPEED_KMH = 25.0

    /** Grobe Zeit für die Übergabe an der Haustür. */
    private const val MINUTES_PER_STOP = 3

    private const val EARTH_RADIUS_KM = 6371.0

    /** Luftlinie zwischen zwei Punkten in Kilometern (Haversine-Formel). */
    fun distanceInKm(from: Coordinates, to: Coordinates): Double {
        val dLat = toRadians(to.latitude - from.latitude)
        val dLng = toRadians(to.longitude - from.longitude)
        val a = sin(dLat / 2).pow(2) +
                cos(toRadians(from.latitude)) * cos(toRadians(to.latitude)) * sin(dLng / 2).pow(2)
        return 2 * EARTH_RADIUS_KM * asin(sqrt(a))
    }

    /** Geschätzte Fahrzeit in Minuten, mindestens 1. */
    fun estimatedMinutes(distanceKm: Double): Int =
        ceil(distanceKm / AVERAGE_SPEED_KMH * 60).toInt().coerceAtLeast(1)

    /** z.B. "1,4 km" oder "350 m" */
    fun formatDistance(distanceKm: Double): String =
        if (distanceKm < 1) "${(distanceKm * 1000).toInt()} m"
        else "${FormatHelper.decimals(distanceKm, 1)} km"

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
        return estimatedMinutes(totalKm) + stopsBefore.size * MINUTES_PER_STOP
    }

    private fun toRadians(degrees: Double): Double = degrees * PI / 180
}
