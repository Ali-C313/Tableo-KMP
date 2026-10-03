package com.example.tableo.data.repository

/** Zugriff auf die Wechselkurs-API. */
interface ExchangeRateRepository {

    /** Holt aktuelle Wechselkurse von Euro in die angegebenen Fremdwährungen, z.B. ["USD", "GBP"]. */
    suspend fun getRates(targetCurrencies: List<String>): Result<Map<String, Double>>
}