package com.example.tableo.data.repository.mock

import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Testdaten für Previews, ohne Firestore. */
class RestaurantRepositoryMock : RestaurantRepository {

    private val restaurants = MutableStateFlow(
        listOf(
            Restaurant(
                id = "1", name = "Layali Lounge", type = "Shisha-Bar",
                address = "Reeperbahn 12, 20359 Hamburg",
                cuisineTypes = listOf("Shisha", "Getränke"),
                openingTime = "16:00", closingTime = "02:00", offersDelivery = false
            ),
            Restaurant(
                id = "2", name = "Burger Haus", type = "Restaurant",
                address = "Schanzenstraße 5, 20357 Hamburg",
                cuisineTypes = listOf("Burger", "Pizza"),
                openingTime = "11:00", closingTime = "23:00"
            )
        )
    )

    override fun getRestaurants() = restaurants.asStateFlow()

    override suspend fun getRestaurantById(restaurantId: String): Restaurant? =
        restaurants.value.find { it.id == restaurantId }
}
