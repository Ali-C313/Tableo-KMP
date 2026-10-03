package com.example.tableo.ui.guest.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Holt die Wechselkurse einmal pro Aufruf der Speisekarte. */
class ExchangeRateViewModel(
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExchangeRateUiState>(ExchangeRateUiState.Idle)
    val uiState = _uiState.asStateFlow()

    /** Lädt die Wechselkurse einmalig, z.B. beim Öffnen der Speisekarte. */
    fun loadRates() {
        if (_uiState.value !is ExchangeRateUiState.Idle) return

        viewModelScope.launch {
            _uiState.update { ExchangeRateUiState.Loading }

            exchangeRateRepository.getRates(listOf("USD", "GBP", "TRY"))
                .onSuccess { rates -> _uiState.update { ExchangeRateUiState.Success(rates) } }
                .onFailure { e ->
                    _uiState.update { ExchangeRateUiState.Error(e.message ?: "Wechselkurse nicht verfügbar") }
                }
        }
    }
}

/** Zustand beim Laden der Wechselkurse. */
sealed interface ExchangeRateUiState {
    data object Idle : ExchangeRateUiState
    data object Loading : ExchangeRateUiState
    data class Success(val rates: Map<String, Double>) : ExchangeRateUiState
    data class Error(val message: String) : ExchangeRateUiState
}