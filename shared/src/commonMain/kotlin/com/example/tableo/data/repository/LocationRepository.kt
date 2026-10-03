package com.example.tableo.data.repository

import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.DeliveryAddress
import kotlinx.coroutines.flow.Flow

/** Zugriff auf GPS und Adresssuche. Die Standort-Berechtigung muss vorher im Screen geholt werden. */
interface LocationRepository {

    /** Aktueller Standort des Geräts samt passender Adresse. */
    suspend fun getCurrentAddress(): Result<DeliveryAddress>

    /** Sucht zu einer eingetippten Adresse die Position. */
    suspend fun searchAddress(query: String): Result<DeliveryAddress>

    /** Laufende Standort-Updates, solange jemand zuhört (für den Fahrer). */
    fun locationUpdates(intervalMillis: Long = 5_000): Flow<Coordinates>
}
