package com.example.tableo.ui.util

import androidx.compose.runtime.Composable

/**
 * Öffnet die Fotoauswahl des Handys. Android nutzt den Photo Picker, iOS den PHPicker.
 *
 * @param onImagePicked Bekommt den Inhalt des gewählten Bildes, null wenn abgebrochen
 * @return Funktion, die die Auswahl öffnet
 */
@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): () -> Unit
