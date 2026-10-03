package com.example.tableo.data.repository

import com.example.tableo.data.enums.ThemeMode

/** Merkt sich lokal auf dem Gerät, ob die App hell, dunkel oder wie das System aussieht. */
interface ThemeRepository {
    suspend fun getThemeMode(): ThemeMode
    suspend fun setThemeMode(mode: ThemeMode)
}
