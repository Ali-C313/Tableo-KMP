package com.example.tableo.data.repository.mock

import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.data.model.DeliveryAddress
import com.example.tableo.data.repository.DeliveryPreferencesRepository

/** Abholung/Lieferung und Adresse für Previews, nur im Speicher. */
class DeliveryPreferencesRepositoryMock(
    private var mode: DeliveryMode = DeliveryMode.ABHOLUNG,
    private var address: DeliveryAddress? = null
) : DeliveryPreferencesRepository {
    override suspend fun getDeliveryMode(): DeliveryMode = mode
    override suspend fun setDeliveryMode(mode: DeliveryMode) { this.mode = mode }
    override suspend fun getAddress(): DeliveryAddress? = address
    override suspend fun setAddress(address: DeliveryAddress) { this.address = address }
}
