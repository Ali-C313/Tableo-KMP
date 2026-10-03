package com.example.tableo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tableo.data.model.Coordinates

/**
 * Karte mit Fahrer und Lieferadresse, beide immer im Bild.
 * Android zeigt Google Maps, das iPhone Apple Maps (MapKit).
 *
 * @param driver Aktueller Standort des Fahrers
 * @param destination Lieferadresse, null wenn unbekannt
 */
@Composable
expect fun TrackingMap(
    driver: Coordinates,
    destination: Coordinates?,
    modifier: Modifier = Modifier
)
