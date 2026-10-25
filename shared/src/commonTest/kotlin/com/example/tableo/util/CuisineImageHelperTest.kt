package com.example.tableo.util

import com.example.tableo.resources.Res
import com.example.tableo.resources.cuisine_burger
import com.example.tableo.resources.cuisine_fruehstueck
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CuisineImageHelperTest {

    @Test
    fun imageFor_ignoresCaseAndSpaces() {
        assertEquals(Res.drawable.cuisine_burger, CuisineImageHelper.imageFor(" BUR ger "))
    }

    @Test
    fun imageFor_knowsUmlautAndSpelling() {
        assertEquals(Res.drawable.cuisine_fruehstueck, CuisineImageHelper.imageFor("Frühstück"))
        assertEquals(Res.drawable.cuisine_fruehstueck, CuisineImageHelper.imageFor("Fruehstueck"))
    }

    @Test
    fun imageFor_unknownCuisine_returnsNull() {
        assertNull(CuisineImageHelper.imageFor("Griechisch"))
    }
}
