package com.example.tableo.ui.guest.delivery

import com.example.tableo.resources.Res
import com.example.tableo.resources.address_error_location
import com.example.tableo.resources.address_error_not_found
import com.example.tableo.resources.address_error_permission_denied
import org.jetbrains.compose.resources.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.data.model.DeliveryAddress
import com.example.tableo.data.repository.DeliveryPreferencesRepository
import com.example.tableo.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Merkt sich Lieferadresse und Abholung/Lieferung für den ganzen Bestellvorgang.
 * Läuft wie der Warenkorb als single in Koin, damit Startseite, Bestelldetails und Warenkorb dasselbe sehen.
 * Beides wird gespeichert und ist nach dem Neustart der App wieder da.
 */
class DeliveryViewModel(
    private val locationRepository: LocationRepository,
    private val deliveryPreferencesRepository: DeliveryPreferencesRepository
) : ViewModel() {

    private val _deliveryMode = MutableStateFlow(DeliveryMode.ABHOLUNG)
    val deliveryMode = _deliveryMode.asStateFlow()

    private val _address = MutableStateFlow<DeliveryAddress?>(null)
    val address = _address.asStateFlow()

    private val _addressState = MutableStateFlow<AddressUiState>(AddressUiState.Idle)
    val addressState = _addressState.asStateFlow()

    init {
        // zuletzt gewählte Art und Adresse vom letzten Mal laden
        viewModelScope.launch {
            _deliveryMode.update { deliveryPreferencesRepository.getDeliveryMode() }
            deliveryPreferencesRepository.getAddress()?.let { saved -> _address.update { saved } }
        }
    }

    fun setDeliveryMode(mode: DeliveryMode) {
        _deliveryMode.update { mode }
        viewModelScope.launch { deliveryPreferencesRepository.setDeliveryMode(mode) }
    }

    /** Holt den aktuellen Standort und macht daraus die Lieferadresse. */
    fun useCurrentLocation() {
        viewModelScope.launch {
            _addressState.update { AddressUiState.Loading }
            locationRepository.getCurrentAddress()
                .onSuccess { found -> saveAddress(found) }
                .onFailure { e -> _addressState.update { AddressUiState.Error(e.message ?: getString(Res.string.address_error_location)) } }
        }
    }

    /** Sucht die eingetippte Adresse und übernimmt sie. */
    fun searchAddress(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _addressState.update { AddressUiState.Loading }
            locationRepository.searchAddress(query.trim())
                .onSuccess { found -> saveAddress(found) }
                .onFailure { e -> _addressState.update { AddressUiState.Error(e.message ?: getString(Res.string.address_error_not_found)) } }
        }
    }

    /** Wenn der Nutzer die Standort-Freigabe ablehnt. */
    fun onLocationPermissionDenied() {
        viewModelScope.launch {
            _addressState.update { AddressUiState.Error(getString(Res.string.address_error_permission_denied)) }
        }
    }

    /** Nach dem Schließen des Dialogs wieder auf Anfang. */
    fun resetAddressState() {
        _addressState.update { AddressUiState.Idle }
    }

    private suspend fun saveAddress(found: DeliveryAddress) {
        _address.update { found }
        _addressState.update { AddressUiState.Success }
        deliveryPreferencesRepository.setAddress(found)
    }
}
