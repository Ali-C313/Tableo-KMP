package com.example.tableo.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OpeningHoursHelperTest {

    @Test
    fun isOpenAt_duringOpeningHours_returnsTrue() {
        assertTrue(OpeningHoursHelper.isOpenAt(12 * 60, "11:00", "22:00"))
    }

    @Test
    fun isOpenAt_beforeOpening_returnsFalse() {
        assertFalse(OpeningHoursHelper.isOpenAt(10 * 60, "11:00", "22:00"))
    }

    @Test
    fun isOpenAt_exactlyAtClosing_returnsFalse() {
        assertFalse(OpeningHoursHelper.isOpenAt(22 * 60, "11:00", "22:00"))
    }

    @Test
    fun isOpenAt_afterMidnight_returnsTrue() {
        assertTrue(OpeningHoursHelper.isOpenAt(60, "18:00", "02:00"))
    }

    @Test
    fun isOpenAt_afternoonWhenOpenOverNight_returnsFalse() {
        assertFalse(OpeningHoursHelper.isOpenAt(15 * 60, "18:00", "02:00"))
    }

    @Test
    fun isOpenAt_invalidFormat_returnsTrue() {
        assertTrue(OpeningHoursHelper.isOpenAt(15 * 60, "abends", ""))
    }
}
