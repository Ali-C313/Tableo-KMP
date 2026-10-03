package com.example.tableo.data.repository

/** Merkt sich dauerhaft (lokal auf dem Gerät), ob das Onboarding schon durchlaufen wurde. */
interface OnboardingRepository {
    suspend fun hasCompletedOnboarding(): Boolean
    suspend fun setOnboardingCompleted()
}