package com.example.tableo.data.repository.mock

import com.example.tableo.data.enums.AppLanguage
import com.example.tableo.data.repository.LanguageRepository

/** Sprache für Previews, nur im Speicher. */
class LanguageRepositoryMock(
    private var language: AppLanguage = AppLanguage.SYSTEM
) : LanguageRepository {
    override suspend fun getLanguage(): AppLanguage = language
    override suspend fun setLanguage(language: AppLanguage) { this.language = language }
}
