package com.example.tableo.ui.staff.team

import com.example.tableo.data.model.StaffProfile

/** Zustand der Team-Übersicht. */
sealed interface StaffTeamUiState {
    data object Loading : StaffTeamUiState
    data class Success(val staff: List<StaffProfile>) : StaffTeamUiState
    data class Error(val message: String) : StaffTeamUiState
}
