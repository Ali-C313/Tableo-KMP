package com.example.tableo.util

import com.example.tableo.data.model.Order
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderHelperTest {

    @Test
    fun orderNumber_takesFirstSixCharsInUppercase() {
        assertEquals("#A1B2C3", OrderHelper.orderNumber(Order(id = "a1b2c3d4e5")))
    }
}
