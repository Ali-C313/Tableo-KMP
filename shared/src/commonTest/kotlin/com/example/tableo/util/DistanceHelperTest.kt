package com.example.tableo.util

import com.example.tableo.data.model.Coordinates
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DistanceHelperTest {

    private val hamburgHbf = Coordinates(53.5528, 10.0067)
    private val jungfernstieg = Coordinates(53.5534, 9.9925)

    @Test
    fun distanceInKm_samePoint_isZero() {
        assertEquals(0.0, DistanceHelper.distanceInKm(hamburgHbf, hamburgHbf))
    }

    @Test
    fun distanceInKm_hbfToJungfernstieg_isAboutOneKm() {
        val km = DistanceHelper.distanceInKm(hamburgHbf, jungfernstieg)
        assertTrue(km in 0.8..1.1, "war $km")
    }

    @Test
    fun estimatedMinutes_isAtLeastOne() {
        assertEquals(1, DistanceHelper.estimatedMinutes(0.0))
    }

    @Test
    fun estimatedMinutes_tenKm_isTwentyFourMinutes() {
        assertEquals(24, DistanceHelper.estimatedMinutes(10.0))
    }

    @Test
    fun formatDistance_underOneKm_showsMeters() {
        assertEquals("350 m", DistanceHelper.formatDistance(0.35))
    }

    @Test
    fun formatDistance_overOneKm_showsKm() {
        assertEquals("1,4 km", DistanceHelper.formatDistance(1.42))
    }

    @Test
    fun estimatedMinutesViaStops_addsTimePerStop() {
        val direct = DistanceHelper.estimatedMinutesViaStops(hamburgHbf, emptyList(), hamburgHbf)
        val withStop = DistanceHelper.estimatedMinutesViaStops(hamburgHbf, listOf(hamburgHbf), hamburgHbf)
        assertEquals(direct + 3, withStop)
    }
}
