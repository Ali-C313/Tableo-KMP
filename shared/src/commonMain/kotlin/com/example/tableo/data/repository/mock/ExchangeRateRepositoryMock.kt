package com.example.tableo.data.repository.mock

import com.example.tableo.data.repository.ExchangeRateRepository

/** Feste Kurse für Previews, ohne API-Aufruf. */
class ExchangeRateRepositoryMock : ExchangeRateRepository {

    override suspend fun getRates(targetCurrencies: List<String>): Result<Map<String, Double>> = runCatching {
        mapOf("USD" to 1.08, "GBP" to 0.85, "TRY" to 35.2).filterKeys { it in targetCurrencies }
    }
}