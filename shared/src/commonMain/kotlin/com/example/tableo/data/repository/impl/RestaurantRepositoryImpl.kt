package com.example.tableo.data.repository.impl

import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.remote.FirestoreCollections
import com.example.tableo.data.repository.RestaurantRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Zugriff auf die Restaurant-Daten in Firestore. */
class RestaurantRepositoryImpl(
    firestore: FirebaseFirestore
) : RestaurantRepository {

    private val restaurantCollection = firestore.collection(FirestoreCollections.RESTAURANTS)

    // snapshots meldet sich bei jeder Änderung in Firestore neu, der Listener endet mit dem Flow
    override fun getRestaurants(): Flow<List<Restaurant>> =
        restaurantCollection.snapshots.map { it.toRestaurants() }

    override suspend fun getRestaurantById(restaurantId: String): Restaurant? {
        if (restaurantId.isBlank()) return null
        val snapshot = restaurantCollection.document(restaurantId).get()
        return if (snapshot.exists) runCatching { snapshot.data<Restaurant>().copy(id = snapshot.id) }.getOrNull() else null
    }
}
