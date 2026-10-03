package com.example.tableo.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/** Dateiname der lokalen Einstellungen. */
const val DATA_STORE_FILE_NAME = "app_settings.preferences_pb"

/**
 * Baut den DataStore. Den Ordner gibt die Plattform vor: auf Android der App-Ordner,
 * auf iOS der Documents-Ordner.
 */
fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
