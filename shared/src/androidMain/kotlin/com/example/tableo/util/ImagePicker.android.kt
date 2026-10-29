package com.example.tableo.util

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

/** Android: Foto-Auswahl über den System-Picker, nur Bilder. */
@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): () -> Unit {
    if (LocalInspectionMode.current) return {}

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Bild gleich als Bytes lesen, damit der gemeinsame Code nichts von Uri wissen muss.
        // Lässt sich die Datei nicht öffnen, kommt null zurück statt eines Absturzes
        val bytes = uri?.let {
            runCatching { context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() } }.getOrNull()
        }
        onImagePicked(bytes)
    }
    return { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
}
