package com.example.tableo.data.repository

import com.example.tableo.data.model.MenuItem
import kotlinx.coroutines.flow.Flow

/** Zugriff auf die Speisekarten-Einträge eines Restaurants. */
interface MenuRepository {

    /** Liefert die Menü-Einträge eines bestimmten Restaurants live als Flow. */
    fun getMenuItems(restaurantId: String): Flow<List<MenuItem>>

    /** Legt einen neuen Artikel an (leere id) oder aktualisiert einen bestehenden (id gesetzt). */
    suspend fun saveMenuItem(menuItem: MenuItem): Result<Unit>

    /** Markiert einen Artikel als (nicht) vorrätig, ohne die übrigen Felder anzufassen. */
    suspend fun setAvailability(menuItemId: String, isAvailable: Boolean): Result<Unit>

    /** Löscht einen Artikel endgültig aus der Speisekarte. */
    suspend fun deleteMenuItem(menuItemId: String): Result<Unit>
}