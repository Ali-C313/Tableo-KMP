package com.example.tableo.data.repository

import com.example.tableo.data.model.Restaurant
import kotlinx.coroutines.flow.Flow

/** Zugriff auf die Restaurant-Daten. */
interface RestaurantRepository {

    /** Liefert alle teilnehmenden Restaurants live als Flow. */
    fun getRestaurants(): Flow<List<Restaurant>>

    /** Lädt ein Restaurant über seine ID (Firestore-Dokument-ID). null, wenn es die ID nicht gibt. */
    suspend fun getRestaurantById(restaurantId: String): Restaurant?
}