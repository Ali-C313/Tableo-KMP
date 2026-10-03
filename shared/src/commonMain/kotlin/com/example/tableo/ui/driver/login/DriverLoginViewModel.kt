package com.example.tableo.ui.driver.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Sucht zum eingegebenen Fahrer-PIN das passende Restaurant. Ist der Fahrer mit seinem Konto
 * angemeldet, wird der Name vom Konto genommen, sonst muss er ihn eintippen.
 */
class DriverLoginViewModel(
    private val restaurantRepository: RestaurantRepository,
    authRepository: AuthRepository
) : ViewModel() {

    /** Name aus dem Konto: Anzeigename, sonst E-Mail vor dem @. null, wenn niemand angemeldet ist. */
    val accountName: String? = authRepository.currentUserName()?.takeIf { it.isNotBlank() }
        ?: authRepository.currentUserEmail()?.substringBefore("@")

    private val _uiState = MutableStateFlow<DriverLoginUiState>(DriverLoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    /** Prüft den Fahrer-PIN gegen die Restaurants in Firestore. */
    fun login(pin: String, name: String, onSuccess: (Restaurant, String) -> Unit) {
        if (name.isBlank()) {
            _uiState.update { DriverLoginUiState.Error("Bitte gib deinen Namen ein") }
            return
        }
        viewModelScope.launch {
            _uiState.update { DriverLoginUiState.Loading }

            val restaurant = runCatching { restaurantRepository.findRestaurantByDriverPin(pin) }.getOrNull()
            if (restaurant != null) {
                _uiState.update { DriverLoginUiState.Idle }
                onSuccess(restaurant, name.trim())
            } else {
                _uiState.update { DriverLoginUiState.Error("Fahrer-PIN nicht gefunden") }
            }
        }
    }
}

/** Zustand beim Prüfen des Fahrer-PINs. */
sealed interface DriverLoginUiState {
    data object Idle : DriverLoginUiState
    data object Loading : DriverLoginUiState
    data class Error(val message: String) : DriverLoginUiState
}
