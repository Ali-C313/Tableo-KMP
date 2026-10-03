package com.example.tableo.data.repository.mock

import com.example.tableo.data.enums.ThemeMode
import com.example.tableo.data.repository.ThemeRepository

/** Erscheinungsbild für Previews, nur im Speicher. */
class ThemeRepositoryMock(
    private var mode: ThemeMode = ThemeMode.SYSTEM
) : ThemeRepository {
    override suspend fun getThemeMode(): ThemeMode = mode
    override suspend fun setThemeMode(mode: ThemeMode) { this.mode = mode }
}
