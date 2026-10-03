package com.example.tableo.data.repository.mock

import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.DeliveryAddress
import com.example.tableo.data.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Testdaten für Previews, ohne GPS. */
class LocationRepositoryMock : LocationRepository {

    private val hamburg = DeliveryAddress(
        text = "Schanzenstraße 5, 20357 Hamburg",
        coordinates = Coordinates(53.5631, 9.9626)
    )

    override suspend fun getCurrentAddress(): Result<DeliveryAddress> = Result.success(hamburg)

    override suspend fun searchAddress(query: String): Result<DeliveryAddress> =
        Result.success(hamburg.copy(text = query))

    override fun locationUpdates(intervalMillis: Long): Flow<Coordinates> = flowOf(hamburg.coordinates)
}
