package com.example.tableo.ui.customer.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.RestaurantRepository
import com.example.tableo.data.repository.StaffRepository
import com.example.tableo.ui.util.currentTimeMillis
import com.example.tableo.ui.util.mapFirebaseAuthError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Login und Registrierung für alle: Kunden und Mitarbeiter nutzen dasselbe Konto-System.
 * Wer beim Registrieren eine Restaurant-ID angibt, wird als Mitarbeiter eingetragen und
 * landet nach dem Login direkt im Dashboard seines Restaurants.
 */
class CustomerAuthViewModel(
    private val authRepository: AuthRepository,
    private val restaurantRepository: RestaurantRepository,
    private val staffRepository: StaffRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CustomerAuthUiState>(CustomerAuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    /**
     * Legt ein Konto an und setzt den Anzeigenamen. Mit Restaurant-ID wird zusätzlich
     * ein Personal-Profil angelegt.
     *
     * @param restaurantId leer = normaler Kunde
     * @param onSuccess bekommt das Restaurant bei Mitarbeitern, sonst null
     */
    fun register(
        email: String,
        password: String,
        name: String,
        restaurantId: String,
        onSuccess: (Restaurant?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { CustomerAuthUiState.Loading }

            // erst prüfen, ob es das Restaurant gibt, bevor ein Konto angelegt wird
            val restaurant = if (restaurantId.isNotBlank()) {
                restaurantRepository.getRestaurantById(restaurantId.trim()) ?: run {
                    _uiState.update { CustomerAuthUiState.Error("Restaurant-ID nicht gefunden") }
                    return@launch
                }
            } else null

            authRepository.register(email, password)
                .onSuccess {
                    // Name ist optional: schlägt das Setzen fehl, ist das Konto trotzdem angelegt
                    authRepository.updateDisplayName(name)
                    if (restaurant != null) {
                        staffRepository.registerStaffProfile(
                            StaffProfile(
                                restaurantId = restaurant.id,
                                userId = authRepository.currentUserId().orEmpty(),
                                name = name,
                                email = email.trim(),
                                joinedAt = currentTimeMillis()
                            )
                        )
                    }
                    _uiState.update { CustomerAuthUiState.Idle }
                    onSuccess(restaurant)
                }
                .onFailure { e -> _uiState.update { CustomerAuthUiState.Error(mapFirebaseAuthError(e)) } }
        }
    }

    /**
     * Meldet an und schaut danach, ob das Konto zu einem Restaurant gehört.
     *
     * @param onSuccess bekommt das Restaurant bei Mitarbeitern, sonst null
     */
    fun login(email: String, password: String, onSuccess: (Restaurant?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { CustomerAuthUiState.Loading }
            authRepository.login(email, password)
                .onSuccess {
                    val restaurant = staffRestaurant()
                    _uiState.update { CustomerAuthUiState.Idle }
                    onSuccess(restaurant)
                }
                .onFailure { e -> _uiState.update { CustomerAuthUiState.Error(mapFirebaseAuthError(e)) } }
        }
    }

    /** Schickt eine E-Mail zum Zurücksetzen des Passworts an die eingegebene Adresse. */
    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.update { CustomerAuthUiState.Loading }
            authRepository.sendPasswordReset(email)
                .onSuccess { _uiState.update { CustomerAuthUiState.ResetEmailSent } }
                .onFailure { e -> _uiState.update { CustomerAuthUiState.Error(mapFirebaseAuthError(e)) } }
        }
    }

    /** Restaurant des angemeldeten Kontos, null wenn es ein normaler Kunde ist. */
    private suspend fun staffRestaurant(): Restaurant? {
        val userId = authRepository.currentUserId() ?: return null
        val profile = runCatching { staffRepository.findProfileForUser(userId) }.getOrNull() ?: return null
        return runCatching { restaurantRepository.getRestaurantById(profile.restaurantId) }.getOrNull()
    }
}

/** Zustand bei Login/Registrierung. */
sealed interface CustomerAuthUiState {
    data object Idle : CustomerAuthUiState
    data object Loading : CustomerAuthUiState
    data object ResetEmailSent : CustomerAuthUiState
    data class Error(val message: String) : CustomerAuthUiState
}
