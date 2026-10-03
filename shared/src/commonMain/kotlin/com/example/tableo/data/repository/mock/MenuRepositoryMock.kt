package com.example.tableo.data.repository.mock

import com.example.tableo.data.model.MenuItem
import com.example.tableo.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Testdaten für Previews, ohne Firestore. */
class MenuRepositoryMock : MenuRepository {

    private val items = MutableStateFlow(
        listOf(
            MenuItem(id = "1", restaurantId = "1", name = "Doppelapfel", description = "Klassiker, mittelstark", price = 12.0, category = "Shisha"),
            MenuItem(id = "2", restaurantId = "1", name = "Minztee", description = "Frisch aufgebrüht", price = 4.5, category = "Getränke"),
            MenuItem(id = "3", restaurantId = "1", name = "Nussmix", description = "Geröstet und gesalzen", price = 6.0, category = "Snacks", available = false)
        )
    )

    override fun getMenuItems(restaurantId: String) = items.asStateFlow()

    override suspend fun saveMenuItem(menuItem: MenuItem): Result<Unit> = runCatching {
        items.value = items.value.filterNot { it.id == menuItem.id } + menuItem
    }

    override suspend fun setAvailability(menuItemId: String, isAvailable: Boolean): Result<Unit> = runCatching {
        items.value = items.value.map { if (it.id == menuItemId) it.copy(available = isAvailable) else it }
    }

    override suspend fun deleteMenuItem(menuItemId: String): Result<Unit> = runCatching {
        items.value = items.value.filterNot { it.id == menuItemId }
    }
}