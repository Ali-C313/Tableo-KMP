package com.example.tableo.data.repository.impl

import com.example.tableo.data.remote.ExchangeRateApi
import com.example.tableo.data.repository.ExchangeRateRepository

/** Zugriff auf die Wechselkurs-API über Ktor. */
class ExchangeRateRepositoryImpl(
    private val api: ExchangeRateApi
) : ExchangeRateRepository {

    override suspend fun getRates(targetCurrencies: List<String>): Result<Map<String, Double>> = runCatching {
        api.getLatestRates(to = targetCurrencies.joinToString(",")).rates
    }
}