package com.example.tableo.ui.guest.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.data.model.DeliveryAddress
import com.example.tableo.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Merkt sich Lieferadresse und Abholung/Lieferung für den ganzen Bestellvorgang.
 * Läuft wie der Warenkorb als single in Koin, damit Startseite, Bestelldetails und Warenkorb dasselbe sehen.
 */
class DeliveryViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _deliveryMode = MutableStateFlow(DeliveryMode.ABHOLUNG)
    val deliveryMode = _deliveryMode.asStateFlow()

    private val _address = MutableStateFlow<DeliveryAddress?>(null)
    val address = _address.asStateFlow()

    private val _addressState = MutableStateFlow<AddressUiState>(AddressUiState.Idle)
    val addressState = _addressState.asStateFlow()

    fun setDeliveryMode(mode: DeliveryMode) {
        _deliveryMode.update { mode }
    }

    /** Holt den aktuellen Standort und macht daraus die Lieferadresse. */
    fun useCurrentLocation() {
        viewModelScope.launch {
            _addressState.update { AddressUiState.Loading }
            locationRepository.getCurrentAddress()
                .onSuccess { found -> saveAddress(found) }
                .onFailure { e -> _addressState.update { AddressUiState.Error(e.message ?: "Standort nicht verfügbar") } }
        }
    }

    /** Sucht die eingetippte Adresse und übernimmt sie. */
    fun searchAddress(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _addressState.update { AddressUiState.Loading }
            locationRepository.searchAddress(query.trim())
                .onSuccess { found -> saveAddress(found) }
                .onFailure { e -> _addressState.update { AddressUiState.Error(e.message ?: "Adresse nicht gefunden") } }
        }
    }

    /** Wenn der Nutzer die Standort-Freigabe ablehnt. */
    fun onLocationPermissionDenied() {
        _addressState.update { AddressUiState.Error("Ohne Standort-Freigabe bitte die Adresse eintippen") }
    }

    /** Nach dem Schließen des Dialogs wieder auf Anfang. */
    fun resetAddressState() {
        _addressState.update { AddressUiState.Idle }
    }

    private fun saveAddress(found: DeliveryAddress) {
        _address.update { found }
        _addressState.update { AddressUiState.Success }
    }
}

/** Zustand beim Suchen der Adresse. */
sealed interface AddressUiState {
    data object Idle : AddressUiState
    data object Loading : AddressUiState
    data object Success : AddressUiState
    data class Error(val message: String) : AddressUiState
}
