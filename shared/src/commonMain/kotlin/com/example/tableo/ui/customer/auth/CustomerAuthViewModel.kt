package com.example.tableo.ui.customer.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.model.StaffAccess
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.RestaurantRepository
import com.example.tableo.data.repository.ACCOUNT_DELETION_GRACE_MILLIS
import com.example.tableo.data.repository.StaffRepository
import com.example.tableo.data.repository.StorageRepository
import com.example.tableo.data.repository.UserRepository
import com.example.tableo.util.currentTimeMillis
import com.example.tableo.util.mapFirebaseAuthError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.tableo.resources.Res
import com.example.tableo.resources.auth_screen_error_deleted
import com.example.tableo.resources.settings_error_restaurant_not_found
import org.jetbrains.compose.resources.getString

/**
 * Login und Registrierung für alle: Kunden und Mitarbeiter nutzen dasselbe Konto-System.
 * Wer beim Registrieren eine Restaurant-ID angibt, wird als Mitarbeiter oder Fahrer eingetragen
 * und landet nach dem Login direkt im Dashboard bzw. im Fahrer-Bereich seines Restaurants.
 */
class CustomerAuthViewModel(
    private val authRepository: AuthRepository,
    private val restaurantRepository: RestaurantRepository,
    private val staffRepository: StaffRepository,
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CustomerAuthUiState>(CustomerAuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    /**
     * Legt ein Konto an und setzt den Anzeigenamen. Mit Restaurant-ID wird zusätzlich
     * ein Personal-Profil angelegt, Fahrer bekommen gleich die Rolle Lieferant.
     *
     * @param restaurantId leer = normaler Kunde
     * @param onSuccess bekommt bei Mitarbeitern Restaurant und Profil, sonst null
     */
    fun register(
        email: String,
        password: String,
        name: String,
        restaurantId: String,
        asDriver: Boolean,
        onSuccess: (StaffAccess?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { CustomerAuthUiState.Loading }

            // erst prüfen, ob es das Restaurant gibt, bevor ein Konto angelegt wird
            val restaurant = if (restaurantId.isNotBlank()) {
                restaurantRepository.getRestaurantById(restaurantId.trim()) ?: run {
                    _uiState.update { CustomerAuthUiState.Error(getString(Res.string.settings_error_restaurant_not_found)) }
                    return@launch
                }
            } else null

            authRepository.register(email, password)
                .onSuccess {
                    // Name ist optional: schlägt das Setzen fehl, ist das Konto trotzdem angelegt
                    authRepository.updateDisplayName(name)
                    val access = restaurant?.let {
                        val profile = StaffProfile(
                            restaurantId = it.id,
                            userId = authRepository.currentUserId().orEmpty(),
                            name = name,
                            email = email.trim(),
                            role = if (asDriver) StaffRole.LIEFERANT else StaffRole.OHNE,
                            joinedAt = currentTimeMillis()
                        )
                        staffRepository.registerStaffProfile(profile)
                        StaffAccess(it, profile)
                    }
                    _uiState.update { CustomerAuthUiState.Idle }
                    onSuccess(access)
                }
                .onFailure { e -> _uiState.update { CustomerAuthUiState.Error(mapFirebaseAuthError(e)) } }
        }
    }

    /**
     * Meldet an und schaut danach, ob das Konto zu einem Restaurant gehört.
     *
     * @param onSuccess bekommt bei Mitarbeitern Restaurant und Profil, sonst null
     */
    fun login(email: String, password: String, onSuccess: (StaffAccess?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { CustomerAuthUiState.Loading }
            authRepository.login(email, password)
                .onSuccess {
                    val userId = authRepository.currentUserId().orEmpty()
                    val requestedAt = runCatching { userRepository.deletionRequestedAt(userId) }.getOrNull()
                    when {
                        requestedAt == null -> {
                            val access = staffAccess()
                            _uiState.update { CustomerAuthUiState.Idle }
                            onSuccess(access)
                        }

                        // innerhalb der 30 Tage: Konto bleibt, Löschen wird zurückgenommen
                        currentTimeMillis() - requestedAt < ACCOUNT_DELETION_GRACE_MILLIS -> {
                            userRepository.clearDeletion(userId)
                            _uiState.update { CustomerAuthUiState.Restored(staffAccess()) }
                        }

                        else -> {
                            deleteForGood(userId)
                            _uiState.update { CustomerAuthUiState.Error(getString(Res.string.auth_screen_error_deleted)) }
                        }
                    }
                }
                .onFailure { e -> _uiState.update { CustomerAuthUiState.Error(mapFirebaseAuthError(e)) } }
        }
    }

    /** Nach dem Hinweis "Konto wiederhergestellt" ganz normal weitermachen. */
    fun continueAfterRestore(onSuccess: (StaffAccess?) -> Unit) {
        val access = (uiState.value as? CustomerAuthUiState.Restored)?.access
        _uiState.update { CustomerAuthUiState.Idle }
        onSuccess(access)
    }

    /** Löscht Team-Einträge, Profilbild, Vormerkung und zuletzt das Firebase-Konto. */
    private suspend fun deleteForGood(userId: String) {
        staffRepository.removeAllForUser(userId)
        // ohne Storage (kein Blaze-Tarif) gibt es kein Bild, das ist kein Fehler
        storageRepository.deleteProfilePhoto(userId)
        userRepository.clearDeletion(userId)
        authRepository.deleteAccount()
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

    /** Restaurant und Profil des angemeldeten Kontos, null wenn es ein normaler Kunde ist. */
    private suspend fun staffAccess(): StaffAccess? {
        val userId = authRepository.currentUserId() ?: return null
        val profile = runCatching { staffRepository.findProfileForUser(userId) }.getOrNull() ?: return null
        val restaurant = runCatching { restaurantRepository.getRestaurantById(profile.restaurantId) }.getOrNull() ?: return null
        return StaffAccess(restaurant, profile)
    }
}
