package com.example.tableo.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Meldet Gäste beim Start anonym an und prüft, ob das Onboarding noch gezeigt werden muss. */
class SplashViewModel(
    private val onboardingRepository: OnboardingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // ohne Anmeldung lassen die Firestore-Regeln keine Bestellung zu
            authRepository.ensureSignedIn()
            val completed = onboardingRepository.hasCompletedOnboarding()
            _uiState.update { SplashUiState.Ready(showOnboarding = !completed) }
        }
    }
}
