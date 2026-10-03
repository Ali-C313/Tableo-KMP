package com.example.tableo.data.repository

import com.example.tableo.data.model.Restaurant
import kotlinx.coroutines.flow.Flow

/** Zugriff auf die Restaurant-Daten. */
interface RestaurantRepository {

    /** Liefert alle teilnehmenden Restaurants live als Flow. */
    fun getRestaurants(): Flow<List<Restaurant>>

    /** Sucht ein Restaurant anhand des Personal-PINs. Gibt null zurück, falls kein Treffer. */
    suspend fun findRestaurantByPin(pin: String): Restaurant?

    /** Lädt ein Restaurant über seine ID (Firestore-Dokument-ID). null, wenn es die ID nicht gibt. */
    suspend fun getRestaurantById(restaurantId: String): Restaurant?

    /** Sucht ein Restaurant anhand des Fahrer-PINs. Gibt null zurück, falls kein Treffer. */
    suspend fun findRestaurantByDriverPin(pin: String): Restaurant?
}