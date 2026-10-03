package com.example.tableo.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.repository.OnboardingRepository
import kotlinx.coroutines.launch

/** Speichert, dass das Onboarding erledigt ist. */
class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    /** Merkt sich dauerhaft, dass das Onboarding durchlaufen wurde, und geht dann weiter. */
    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            onboardingRepository.setOnboardingCompleted()
            onDone()
        }
    }
}