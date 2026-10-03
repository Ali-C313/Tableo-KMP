package com.example.tableo.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.tableo.data.repository.OnboardingRepository
import kotlinx.coroutines.flow.first

private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")

/**
 * Speichert per DataStore, ob das Onboarding schon gezeigt wurde. Bleibt auch nach App-Neustart erhalten.
 * Wo die Datei liegt, entscheidet die jeweilige Plattform (siehe createDataStore).
 */
class OnboardingRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : OnboardingRepository {

    override suspend fun hasCompletedOnboarding(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[ONBOARDING_COMPLETED_KEY] ?: false
    }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = true
        }
    }
}
