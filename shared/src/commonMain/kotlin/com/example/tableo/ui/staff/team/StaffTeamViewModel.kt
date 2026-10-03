package com.example.tableo.ui.staff.team

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.StaffRepository
import com.example.tableo.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Lädt das Team eines Restaurants, vergibt Rollen und entfernt Mitarbeiter. */
class StaffTeamViewModel(
    savedStateHandle: SavedStateHandle,
    private val staffRepository: StaffRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.StaffTeam>()
    val restaurantId: String = route.restaurantId
    val restaurantName: String = route.restaurantName

    /** Eigenes Konto, damit man sich nicht selbst aus dem Team wirft. */
    val currentUserId: String? get() = authRepository.currentUserId()

    private val _uiState = MutableStateFlow<StaffTeamUiState>(StaffTeamUiState.Loading)
    val uiState = _uiState.asStateFlow()

    /** Fehler beim Ändern der Rolle oder Entfernen, null wenn alles ok. */
    private val _actionError = MutableStateFlow<String?>(null)
    val actionError = _actionError.asStateFlow()

    init {
        loadTeam()
    }

    /** Lädt alle Team-Mitglieder live, neue Rollen und Bilder kommen direkt an. */
    private fun loadTeam() {
        viewModelScope.launch {
            staffRepository.getStaffForRestaurant(restaurantId)
                .catch { e -> _uiState.update { StaffTeamUiState.Error(e.message ?: "Team konnte nicht geladen werden") } }
                .collect { staff ->
                    // nach Rolle sortiert, ohne Rolle ganz unten
                    val sorted = staff.sortedWith(
                        compareBy({ if (it.role == StaffRole.OHNE) Int.MAX_VALUE else it.role.ordinal }, { it.name })
                    )
                    _uiState.update { StaffTeamUiState.Success(sorted) }
                }
        }
    }

    fun updateRole(profile: StaffProfile, role: StaffRole) {
        if (profile.role == role) return
        viewModelScope.launch {
            staffRepository.updateRole(profile.id, role)
                .onSuccess { _actionError.update { null } }
                .onFailure { e -> _actionError.update { e.message ?: "Rolle konnte nicht geändert werden" } }
        }
    }

    /** Wirft den Mitarbeiter aus dem Team, sein Konto bleibt bestehen. */
    fun removeStaff(profile: StaffProfile, onDone: () -> Unit) {
        viewModelScope.launch {
            staffRepository.removeStaff(profile.id)
                .onSuccess {
                    _actionError.update { null }
                    onDone()
                }
                .onFailure { e -> _actionError.update { e.message ?: "Mitarbeiter konnte nicht entfernt werden" } }
        }
    }

    fun clearActionError() {
        _actionError.update { null }
    }
}

/** Zustand der Team-Übersicht. */
sealed interface StaffTeamUiState {
    data object Loading : StaffTeamUiState
    data class Success(val staff: List<StaffProfile>) : StaffTeamUiState
    data class Error(val message: String) : StaffTeamUiState
}
