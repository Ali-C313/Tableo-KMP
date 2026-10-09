package com.example.tableo.data.repository

import com.example.tableo.data.enums.AppLanguage

/** Merkt sich lokal auf dem Gerät, in welcher Sprache die App läuft. */
interface LanguageRepository {
    suspend fun getLanguage(): AppLanguage
    suspend fun setLanguage(language: AppLanguage)
}
