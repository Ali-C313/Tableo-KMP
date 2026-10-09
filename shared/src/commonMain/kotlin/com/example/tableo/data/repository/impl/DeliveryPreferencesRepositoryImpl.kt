package com.example.tableo.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.DeliveryAddress
import com.example.tableo.data.repository.DeliveryPreferencesRepository
import kotlinx.coroutines.flow.first

private val DELIVERY_MODE_KEY = stringPreferencesKey("delivery_mode")
private val ADDRESS_TEXT_KEY = stringPreferencesKey("delivery_address_text")
private val ADDRESS_LAT_KEY = doublePreferencesKey("delivery_address_lat")
private val ADDRESS_LNG_KEY = doublePreferencesKey("delivery_address_lng")

/** Speichert Abholung/Lieferung und Lieferadresse per DataStore auf dem Gerät. */
class DeliveryPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DeliveryPreferencesRepository {

    override suspend fun getDeliveryMode(): DeliveryMode {
        val saved = dataStore.data.first()[DELIVERY_MODE_KEY]
        return DeliveryMode.entries.find { it.name == saved } ?: DeliveryMode.ABHOLUNG
    }

    override suspend fun setDeliveryMode(mode: DeliveryMode) {
        dataStore.edit { it[DELIVERY_MODE_KEY] = mode.name }
    }

    override suspend fun getAddress(): DeliveryAddress? {
        val preferences = dataStore.data.first()
        val text = preferences[ADDRESS_TEXT_KEY]?.takeIf { it.isNotBlank() } ?: return null
        return DeliveryAddress(
            text = text,
            coordinates = Coordinates(
                latitude = preferences[ADDRESS_LAT_KEY] ?: 0.0,
                longitude = preferences[ADDRESS_LNG_KEY] ?: 0.0
            )
        )
    }

    override suspend fun setAddress(address: DeliveryAddress) {
        dataStore.edit {
            it[ADDRESS_TEXT_KEY] = address.text
            it[ADDRESS_LAT_KEY] = address.coordinates.latitude
            it[ADDRESS_LNG_KEY] = address.coordinates.longitude
        }
    }
}
