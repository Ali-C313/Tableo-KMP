package com.example.tableo.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tableo.data.enums.ThemeMode
import com.example.tableo.data.repository.ThemeRepository
import kotlinx.coroutines.flow.first

private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

/** Speichert das Erscheinungsbild per DataStore, damit es nach dem Neustart wieder da ist. */
class ThemeRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {

    override suspend fun getThemeMode(): ThemeMode {
        val saved = dataStore.data.first()[THEME_MODE_KEY]
        return ThemeMode.entries.find { it.name == saved } ?: ThemeMode.SYSTEM
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
}
