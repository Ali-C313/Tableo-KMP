package com.example.tableo.util

import com.example.tableo.data.model.Order
import com.example.tableo.data.model.OrderItem
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderHelperTest {

    @Test
    fun orderNumber_takesFirstSixCharsInUppercase() {
        assertEquals("#A1B2C3", OrderHelper.orderNumber(Order(id = "a1b2c3d4e5")))
    }

    @Test
    fun itemsText_listsQuantityAndName() {
        val order = Order(
            items = listOf(
                OrderItem(menuItemId = "1", name = "Doppelapfel", pricePerItem = 12.0, quantity = 2),
                OrderItem(menuItemId = "2", name = "Minztee", pricePerItem = 4.5, quantity = 1)
            )
        )
        assertEquals("2× Doppelapfel, 1× Minztee", OrderHelper.itemsText(order))
    }
}
