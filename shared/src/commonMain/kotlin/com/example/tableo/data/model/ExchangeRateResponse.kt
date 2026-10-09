package com.example.tableo.data.model

import kotlinx.serialization.Serializable

/** Antwort der Wechselkurs-API (frankfurter.app). */
@Serializable
data class ExchangeRateResponse(
    val amount: Double = 0.0,
    val base: String = "",
    val date: String = "",
    val rates: Map<String, Double> = emptyMap()
)
