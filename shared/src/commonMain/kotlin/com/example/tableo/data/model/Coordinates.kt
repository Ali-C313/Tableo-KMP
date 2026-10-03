package com.example.tableo.data.model

import kotlinx.serialization.Serializable

/** Ein Punkt auf der Karte, z.B. der aktuelle Standort des Fahrers. */
@Serializable
data class Coordinates(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
