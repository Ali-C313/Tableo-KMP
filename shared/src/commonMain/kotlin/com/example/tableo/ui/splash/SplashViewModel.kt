package com.example.tableo.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Prüft beim Start, ob das Onboarding noch gezeigt werden muss. */
class SplashViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val completed = onboardingRepository.hasCompletedOnboarding()
            _uiState.update { SplashUiState.Ready(showOnboarding = !completed) }
        }
    }
}

/** Zustand beim Start: Loading bis geklärt ist, ob das Onboarding schon gezeigt wurde. */
sealed interface SplashUiState {
    data object Loading : SplashUiState
    data class Ready(val showOnboarding: Boolean) : SplashUiState
}