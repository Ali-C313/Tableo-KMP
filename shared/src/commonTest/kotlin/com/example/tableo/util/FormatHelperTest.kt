package com.example.tableo.util

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatHelperTest {

    @Test
    fun euro_withWholeNumber_addsTwoDecimals() {
        assertEquals("13,00 €", FormatHelper.euro(13.0))
    }

    @Test
    fun euro_roundsToCents() {
        assertEquals("9,99 €", FormatHelper.euro(9.989))
    }

    @Test
    fun decimals_withNegativeValue_keepsSign() {
        assertEquals("-2,50", FormatHelper.decimals(-2.5, 2))
    }

    @Test
    fun foreignCurrency_usesSymbolAndDot() {
        assertEquals("$14.07", FormatHelper.foreignCurrency(14.07, "USD"))
    }

    @Test
    fun foreignCurrency_unknownCode_showsCode() {
        assertEquals("CHF3.50", FormatHelper.foreignCurrency(3.5, "CHF"))
    }

    @Test
    fun twoDigits_addsLeadingZero() {
        assertEquals("07", FormatHelper.twoDigits(7))
        assertEquals("12", FormatHelper.twoDigits(12))
    }

    @Test
    fun firstName_returnsOnlyFirstName() {
        assertEquals("Omar", FormatHelper.firstName("  Omar Haddad "))
    }
}
