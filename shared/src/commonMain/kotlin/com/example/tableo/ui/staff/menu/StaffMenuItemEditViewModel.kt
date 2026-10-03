package com.example.tableo.ui.staff.menu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tableo.data.model.MenuItem
import com.example.tableo.data.repository.MenuRepository
import com.example.tableo.data.repository.StorageRepository
import com.example.tableo.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Artikel anlegen oder bearbeiten, inklusive Foto-Upload. */
class StaffMenuItemEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val menuRepository: MenuRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.StaffMenuItemEdit>()
    val restaurantId: String = route.restaurantId
    val menuItemId: String? = route.menuItemId

    private val _uiState = MutableStateFlow<StaffMenuItemEditUiState>(StaffMenuItemEditUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _prefill = MutableStateFlow<MenuItem?>(null)
    val prefill = _prefill.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories = _categories.asStateFlow()

    init {
        loadCategories()
        loadForEditing()
    }

    /** Lädt die bereits vorhandenen Kategorien dieses Restaurants, für den Kategorie-Picker. */
    private fun loadCategories() {
        viewModelScope.launch {
            val items = menuRepository.getMenuItems(restaurantId).first()
            _categories.update { items.map { it.category }.filter { it.isNotBlank() }.distinct().sorted() }
        }
    }

    /** Lädt den zu bearbeitenden Artikel, falls eine menuItemId in der Route steckt (sonst Neuanlage). */
    private fun loadForEditing() {
        val id = menuItemId ?: return
        viewModelScope.launch {
            val items = menuRepository.getMenuItems(restaurantId).first()
            _prefill.update { items.find { it.id == id } }
        }
    }

    /**
     * Speichert den Artikel. Ist [newImageBytes] gesetzt (Nutzer hat ein neues Foto gewählt),
     * wird es zuerst zu Firebase Storage hochgeladen und die entstehende URL verwendet,
     * sonst bleibt die bisherige [MenuItem.imageUrl] unverändert.
     */
    fun save(menuItem: MenuItem, newImageBytes: ByteArray?) {
        viewModelScope.launch {
            _uiState.update { StaffMenuItemEditUiState.Saving }

            val finalImageUrl = if (newImageBytes != null) {
                val uploadResult = storageRepository.uploadMenuItemImage(restaurantId, newImageBytes)
                val uploadedUrl = uploadResult.getOrNull()
                if (uploadedUrl == null) {
                    _uiState.update {
                        StaffMenuItemEditUiState.Error(
                            uploadResult.exceptionOrNull()?.message ?: "Bild-Upload fehlgeschlagen"
                        )
                    }
                    return@launch
                }
                uploadedUrl
            } else {
                menuItem.imageUrl
            }

            menuRepository.saveMenuItem(menuItem.copy(imageUrl = finalImageUrl))
                .onSuccess { _uiState.update { StaffMenuItemEditUiState.Saved } }
                .onFailure { e -> _uiState.update { StaffMenuItemEditUiState.Error(e.message ?: "Speichern fehlgeschlagen") } }
        }
    }
}

/** Zustand beim Speichern eines Speisekarten-Artikels. */
sealed interface StaffMenuItemEditUiState {
    data object Idle : StaffMenuItemEditUiState
    data object Saving : StaffMenuItemEditUiState
    data object Saved : StaffMenuItemEditUiState
    data class Error(val message: String) : StaffMenuItemEditUiState
}