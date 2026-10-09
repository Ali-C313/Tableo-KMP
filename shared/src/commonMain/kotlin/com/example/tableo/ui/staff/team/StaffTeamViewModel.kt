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
import com.example.tableo.resources.Res
import com.example.tableo.resources.team_error_load
import com.example.tableo.resources.team_error_role
import com.example.tableo.resources.team_error_remove
import org.jetbrains.compose.resources.getString

/** Lädt das Team eines Restaurants. Rollen vergeben und Mitarbeiter entfernen darf nur die Leitung. */
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
        viewModelScope.launch {
            // alte Einträge mit zufälliger ID umziehen, die Firestore-Regeln brauchen die User-ID
            staffRepository.migrateRestaurant(restaurantId)
        }
        loadTeam()
    }

    /** true, wenn das angemeldete Konto in diesem Team die Leitung hat. */
    fun canManage(staff: List<StaffProfile>): Boolean =
        staff.any { it.userId == currentUserId && it.role == StaffRole.LEITUNG }

    /** Lädt alle Team-Mitglieder live, neue Rollen und Bilder kommen direkt an. */
    private fun loadTeam() {
        viewModelScope.launch {
            staffRepository.getStaffForRestaurant(restaurantId)
                .catch { e -> _uiState.update { StaffTeamUiState.Error(e.message ?: getString(Res.string.team_error_load)) } }
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
                .onFailure { e -> _actionError.update { e.message ?: getString(Res.string.team_error_role) } }
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
                .onFailure { e -> _actionError.update { e.message ?: getString(Res.string.team_error_remove) } }
        }
    }

    fun clearActionError() {
        _actionError.update { null }
    }
}
