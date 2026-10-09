package com.example.tableo.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tableo.data.enums.AppLanguage
import com.example.tableo.data.repository.LanguageRepository
import kotlinx.coroutines.flow.first

private val LANGUAGE_KEY = stringPreferencesKey("app_language")

/** Speichert die Sprache per DataStore, leer = Sprache vom Handy. */
class LanguageRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : LanguageRepository {

    override suspend fun getLanguage(): AppLanguage =
        AppLanguage.fromCode(dataStore.data.first()[LANGUAGE_KEY]?.ifBlank { null })

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code.orEmpty()
        }
    }
}
