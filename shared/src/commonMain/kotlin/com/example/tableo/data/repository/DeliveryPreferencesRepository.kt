package com.example.tableo.data.repository

import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.data.model.DeliveryAddress

/** Merkt sich lokal Abholung/Lieferung und die letzte Lieferadresse, auch nach dem Schließen der App. */
interface DeliveryPreferencesRepository {
    suspend fun getDeliveryMode(): DeliveryMode
    suspend fun setDeliveryMode(mode: DeliveryMode)

    /** Letzte Lieferadresse, null wenn noch keine eingegeben wurde. */
    suspend fun getAddress(): DeliveryAddress?
    suspend fun setAddress(address: DeliveryAddress)
}
