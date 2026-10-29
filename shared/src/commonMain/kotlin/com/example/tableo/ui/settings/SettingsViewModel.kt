package com.example.tableo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.enums.AppLanguage
import com.example.tableo.data.enums.ThemeMode
import com.example.tableo.data.model.StaffAccess
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.RestaurantRepository
import com.example.tableo.data.repository.StaffRepository
import com.example.tableo.data.repository.StorageRepository
import com.example.tableo.data.repository.LanguageRepository
import com.example.tableo.data.repository.ThemeRepository
import com.example.tableo.util.AuthErrorHelper
import com.example.tableo.util.FormatHelper
import com.example.tableo.util.LanguageState
import com.example.tableo.data.repository.UserRepository
import com.example.tableo.ui.theme.ThemeState
import com.example.tableo.util.currentTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.tableo.resources.Res
import com.example.tableo.resources.settings_error_link_failed
import com.example.tableo.resources.settings_error_photo_upload
import com.example.tableo.resources.settings_error_restaurant_not_found
import org.jetbrains.compose.resources.getString

/** Kontodaten anzeigen und ändern, Abmelden und Konto löschen. Erkennt, ob das Konto ein Mitarbeiter oder Fahrer ist. */
class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val staffRepository: StaffRepository,
    private val restaurantRepository: RestaurantRepository,
    private val storageRepository: StorageRepository,
    private val themeRepository: ThemeRepository,
    private val userRepository: UserRepository,
    private val languageRepository: LanguageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState = _uiState.asStateFlow()

    /** Restaurant und Profil, wenn der angemeldete Nutzer dort arbeitet. null bei Kunden. */
    private val _staffAccess = MutableStateFlow<StaffAccess?>(null)
    val staffAccess = _staffAccess.asStateFlow()

    /** Fehler beim Löschen des Kontos, null wenn alles ok. */
    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError = _deleteError.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting = _isDeleting.asStateFlow()

    /** Link zum Profilbild, leer wenn noch keins hochgeladen wurde. */
    private val _photoUrl = MutableStateFlow(authRepository.currentUserPhotoUrl().orEmpty())
    val photoUrl = _photoUrl.asStateFlow()

    /** true, solange ein neues Profilbild hochgeladen wird. */
    private val _isUploadingPhoto = MutableStateFlow(false)
    val isUploadingPhoto = _isUploadingPhoto.asStateFlow()

    init {
        loadStaffAccess()
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
                .onFailure { e -> _uiState.update { SettingsUiState.Error(e.message ?: getString(Res.string.settings_error_photo_upload)) } }
            _isUploadingPhoto.update { false }
        }
    }

    /** Fehlertext beim Verknüpfen mit einem Restaurant, null wenn alles ok. */
    private val _linkError = MutableStateFlow<String?>(null)
    val linkError = _linkError.asStateFlow()

    /**
     * Verknüpft ein bestehendes Konto nachträglich mit einem Restaurant, z.B. für Konten,
     * die schon vor der Mitarbeiter-Registrierung angelegt wurden.
     *
     * @param asDriver true = Rolle Lieferant, landet dann im Fahrer-Bereich
     */
    fun linkRestaurant(restaurantId: String, asDriver: Boolean, onDone: () -> Unit) {
        val userId = authRepository.currentUserId() ?: return
        viewModelScope.launch {
            val restaurant = runCatching { restaurantRepository.getRestaurantById(restaurantId.trim()) }.getOrNull()
            if (restaurant == null) {
                _linkError.update { getString(Res.string.settings_error_restaurant_not_found) }
                return@launch
            }
            val name = FormatHelper.displayName(currentName, currentEmail).orEmpty()
            val profile = StaffProfile(
                restaurantId = restaurant.id,
                userId = userId,
                name = name,
                email = currentEmail.orEmpty(),
                photoUrl = _photoUrl.value,
                role = if (asDriver) StaffRole.LIEFERANT else StaffRole.OHNE,
                joinedAt = currentTimeMillis()
            )
            staffRepository.registerStaffProfile(profile)
                .onSuccess {
                    _linkError.update { null }
                    _staffAccess.update { StaffAccess(restaurant, profile) }
                    onDone()
                }
                .onFailure { e -> _linkError.update { e.message ?: getString(Res.string.settings_error_link_failed) } }
        }
    }

    /** Stellt das Erscheinungsbild sofort um und speichert es für den nächsten Start. */
    fun setThemeMode(mode: ThemeMode) {
        ThemeState.mode = mode
        viewModelScope.launch { themeRepository.setThemeMode(mode) }
    }

    /** Stellt die Sprache sofort um und speichert sie für den nächsten Start. */
    fun setLanguage(language: AppLanguage) {
        LanguageState.language = language
        viewModelScope.launch { languageRepository.setLanguage(language) }
    }

    /** Blendet den Fehler beim Verknüpfen wieder aus. */
    fun clearLinkError() {
        _linkError.update { null }
    }

    private fun loadStaffAccess() {
        val userId = authRepository.currentUserId() ?: return
        viewModelScope.launch {
            val profile = runCatching { staffRepository.findProfileForUser(userId) }.getOrNull() ?: return@launch
            val restaurant = runCatching { restaurantRepository.getRestaurantById(profile.restaurantId) }.getOrNull() ?: return@launch
            _staffAccess.update { StaffAccess(restaurant, profile) }
        }
    }

    /** Name, E-Mail und Login-Status direkt aus Firebase, für die Anzeige. */
    val currentName: String? get() = authRepository.currentUserName()
    val currentEmail: String? get() = authRepository.currentUserEmail()
    val isLoggedIn: Boolean get() = authRepository.currentUserId() != null

    /** Meldet den Kunden ab und ruft danach [onDone] auf. */
    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _staffAccess.update { null }
            _photoUrl.update { "" }
            onDone()
        }
    }

    /**
     * Merkt das Konto zum Löschen vor und meldet ab. Wer sich innerhalb von 30 Tagen wieder
     * anmeldet, behält sein Konto. Danach wird es beim nächsten Anmeldeversuch endgültig gelöscht.
     * Firebase verlangt einen frischen Login, deshalb wird das Passwort abgefragt.
     */
    fun deleteAccount(currentPassword: String, onDone: () -> Unit) {
        val userId = authRepository.currentUserId() ?: return
        viewModelScope.launch {
            _isDeleting.update { true }
            val result = runCatching {
                authRepository.reauthenticate(currentPassword).getOrThrow()
                userRepository.requestDeletion(userId).getOrThrow()
                // im Team nicht mehr als "im Dienst" stehen bleiben
                _staffAccess.value?.let { staffRepository.setOnDuty(it.profile.id, false) }
                authRepository.logout()
            }
            _isDeleting.update { false }
            result
                .onSuccess {
                    _deleteError.update { null }
                    _staffAccess.update { null }
                    _photoUrl.update { "" }
                    onDone()
                }
                .onFailure { e -> _deleteError.update { AuthErrorHelper.message(e) } }
        }
    }

    /** Blendet den Fehler beim Konto löschen wieder aus. */
    fun clearDeleteError() {
        _deleteError.update { null }
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
                .onFailure { e -> _uiState.update { SettingsUiState.Error(AuthErrorHelper.message(e)) } }
        }
    }
}
