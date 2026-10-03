package com.example.tableo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.RestaurantRepository
import com.example.tableo.data.repository.StaffRepository
import com.example.tableo.data.repository.StorageRepository
import com.example.tableo.ui.util.currentTimeMillis
import com.example.tableo.ui.util.mapFirebaseAuthError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Kontodaten anzeigen und ändern, außerdem Abmelden. Erkennt, ob das Konto ein Mitarbeiter ist. */
class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val staffRepository: StaffRepository,
    private val restaurantRepository: RestaurantRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState = _uiState.asStateFlow()

    /** Restaurant, in dem der angemeldete Nutzer arbeitet. null bei Kunden. */
    private val _staffRestaurant = MutableStateFlow<Restaurant?>(null)
    val staffRestaurant = _staffRestaurant.asStateFlow()

    /** Link zum Profilbild, leer wenn noch keins hochgeladen wurde. */
    private val _photoUrl = MutableStateFlow(authRepository.currentUserPhotoUrl().orEmpty())
    val photoUrl = _photoUrl.asStateFlow()

    /** true, solange ein neues Profilbild hochgeladen wird. */
    private val _isUploadingPhoto = MutableStateFlow(false)
    val isUploadingPhoto = _isUploadingPhoto.asStateFlow()

    init {
        loadStaffRestaurant()
    }

    /**
     * Lädt das Bild hoch, hängt es ans Konto und aktualisiert es auch im Team-Profil,
     * damit der Chef es in der Team-Liste sieht.
     */
    fun updateProfilePhoto(imageBytes: ByteArray?) {
        val userId = authRepository.currentUserId() ?: return
        if (imageBytes == null) return
        viewModelScope.launch {
            _isUploadingPhoto.update { true }
            storageRepository.uploadProfilePhoto(userId, imageBytes)
                .onSuccess { url ->
                    authRepository.updatePhotoUrl(url)
                    staffRepository.updatePhotoForUser(userId, url)
                    _photoUrl.update { url }
                }
                .onFailure { e -> _uiState.update { SettingsUiState.Error(e.message ?: "Bild konnte nicht hochgeladen werden") } }
            _isUploadingPhoto.update { false }
        }
    }

    /** Fehlertext beim Verknüpfen mit einem Restaurant, null wenn alles ok. */
    private val _linkError = MutableStateFlow<String?>(null)
    val linkError = _linkError.asStateFlow()

    /**
     * Verknüpft ein bestehendes Konto nachträglich mit einem Restaurant, z.B. für Konten,
     * die schon vor der Mitarbeiter-Registrierung angelegt wurden.
     */
    fun linkRestaurant(restaurantId: String, onDone: () -> Unit) {
        val userId = authRepository.currentUserId() ?: return
        viewModelScope.launch {
            val restaurant = runCatching { restaurantRepository.getRestaurantById(restaurantId.trim()) }.getOrNull()
            if (restaurant == null) {
                _linkError.update { "Restaurant-ID nicht gefunden" }
                return@launch
            }
            val name = currentName?.takeIf { it.isNotBlank() } ?: currentEmail?.substringBefore("@").orEmpty()
            staffRepository.registerStaffProfile(
                StaffProfile(
                    restaurantId = restaurant.id,
                    userId = userId,
                    name = name,
                    email = currentEmail.orEmpty(),
                    photoUrl = _photoUrl.value,
                    joinedAt = currentTimeMillis()
                )
            )
                .onSuccess {
                    _linkError.update { null }
                    _staffRestaurant.update { restaurant }
                    onDone()
                }
                .onFailure { e -> _linkError.update { e.message ?: "Verknüpfen hat nicht geklappt" } }
        }
    }

    fun clearLinkError() {
        _linkError.update { null }
    }

    private fun loadStaffRestaurant() {
        val userId = authRepository.currentUserId() ?: return
        viewModelScope.launch {
            val profile = runCatching { staffRepository.findProfileForUser(userId) }.getOrNull() ?: return@launch
            val restaurant = runCatching { restaurantRepository.getRestaurantById(profile.restaurantId) }.getOrNull()
            _staffRestaurant.update { restaurant }
        }
    }

    val currentName: String? get() = authRepository.currentUserName()
    val currentEmail: String? get() = authRepository.currentUserEmail()
    val isLoggedIn: Boolean get() = authRepository.currentUserId() != null

    /** Meldet den Kunden ab und ruft danach [onDone] auf. */
    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _staffRestaurant.update { null }
            _photoUrl.update { "" }
            onDone()
        }
    }

    /** Ändert nur den Anzeigenamen. */
    fun updateName(name: String) {
        viewModelScope.launch {
            _uiState.update { SettingsUiState.Saving }
            authRepository.updateDisplayName(name)
                .onSuccess { _uiState.update { SettingsUiState.Saved } }
                .onFailure { e -> _uiState.update { SettingsUiState.Error(mapFirebaseAuthError(e)) } }
        }
    }

    /**
     * Speichert Name, E-Mail und Passwort nacheinander, aber nur was sich geändert hat.
     * Für E-Mail und Passwort meldet sich der Nutzer vorher mit dem aktuellen Passwort neu an,
     * das verlangt Firebase bei solchen Änderungen.
     */
    fun saveAccountInfo(name: String, email: String, currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { SettingsUiState.Saving }
            val emailChanged = email != (currentEmail ?: "")
            val result = runCatching {
                if (name != (currentName ?: "")) {
                    authRepository.updateDisplayName(name).getOrThrow()
                }
                if (emailChanged || newPassword.isNotBlank()) {
                    authRepository.reauthenticate(currentPassword).getOrThrow()
                    if (emailChanged) authRepository.updateEmail(email).getOrThrow()
                    if (newPassword.isNotBlank()) authRepository.updatePassword(newPassword).getOrThrow()
                }
            }
            result
                .onSuccess { _uiState.update { SettingsUiState.Saved } }
                .onFailure { e -> _uiState.update { SettingsUiState.Error(mapFirebaseAuthError(e)) } }
        }
    }

    /** Meldet sich zuerst mit dem aktuellen Passwort erneut an, ändert danach die E-Mail. */
    fun updateEmail(newEmail: String, currentPassword: String) {
        viewModelScope.launch {
            _uiState.update { SettingsUiState.Saving }
            authRepository.reauthenticate(currentPassword)
                .onSuccess {
                    authRepository.updateEmail(newEmail)
                        .onSuccess { _uiState.update { SettingsUiState.Saved } }
                        .onFailure { e -> _uiState.update { SettingsUiState.Error(mapFirebaseAuthError(e)) } }
                }
                .onFailure { e -> _uiState.update { SettingsUiState.Error(mapFirebaseAuthError(e)) } }
        }
    }

    /** Meldet sich zuerst mit dem aktuellen Passwort erneut an, ändert danach das Passwort. */
    fun updatePassword(newPassword: String, currentPassword: String) {
        viewModelScope.launch {
            _uiState.update { SettingsUiState.Saving }
            authRepository.reauthenticate(currentPassword)
                .onSuccess {
                    authRepository.updatePassword(newPassword)
                        .onSuccess { _uiState.update { SettingsUiState.Saved } }
                        .onFailure { e -> _uiState.update { SettingsUiState.Error(mapFirebaseAuthError(e)) } }
                }
                .onFailure { e -> _uiState.update { SettingsUiState.Error(mapFirebaseAuthError(e)) } }
        }
    }
}

/** Zustand beim Speichern von Name, E-Mail oder Passwort. */
sealed interface SettingsUiState {
    data object Idle : SettingsUiState
    data object Saving : SettingsUiState
    data object Saved : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}