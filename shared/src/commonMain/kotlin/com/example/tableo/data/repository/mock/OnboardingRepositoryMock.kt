package com.example.tableo.data.repository.mock

import com.example.tableo.data.repository.OnboardingRepository

/** Onboarding-Status für Previews. */
class OnboardingRepositoryMock(
    private var completed: Boolean = false
) : OnboardingRepository {
    override suspend fun hasCompletedOnboarding(): Boolean = completed
    override suspend fun setOnboardingCompleted() { completed = true }
}