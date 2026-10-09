package com.example.tableo.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.example.tableo.data.model.ExchangeRateResponse

private const val BASE_URL = "https://api.frankfurter.app/"

/**
 * Wechselkurs-API (frankfurter.app) über Ktor. Die Engine (OkHttp auf Android, Darwin auf iOS)
 * sucht sich Ktor selbst aus.
 */
class ExchangeRateApi {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    /** Kurse von [from] in die Währungen aus [to], z.B. "USD,GBP". */
    suspend fun getLatestRates(from: String = "EUR", to: String): ExchangeRateResponse =
        client.get("${BASE_URL}latest") {
            parameter("from", from)
            parameter("to", to)
        }.body()
}
