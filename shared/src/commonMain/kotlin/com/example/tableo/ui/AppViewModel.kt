package com.example.tableo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.repository.LanguageRepository
import com.example.tableo.data.repository.ThemeRepository
import com.example.tableo.ui.theme.ThemeState
import com.example.tableo.util.LanguageState
import kotlinx.coroutines.launch

/** Lädt beim Start das gespeicherte Erscheinungsbild und die gespeicherte Sprache. */
class AppViewModel(
    private val themeRepository: ThemeRepository,
    private val languageRepository: LanguageRepository
) : ViewModel() {

    /** Setzt Hell/Dunkel und Sprache so, wie sie zuletzt eingestellt waren. */
    fun loadSettings() {
        viewModelScope.launch {
            ThemeState.mode = themeRepository.getThemeMode()
            LanguageState.language = languageRepository.getLanguage()
        }
    }
}
