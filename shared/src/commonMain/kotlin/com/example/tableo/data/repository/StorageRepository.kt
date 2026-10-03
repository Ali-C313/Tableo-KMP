package com.example.tableo.data.repository

/** Zugriff auf Firebase Storage zum Hochladen von Speisekarten-Fotos. */
interface StorageRepository {

    /**
     * Lädt ein ausgewähltes Foto hoch und gibt die öffentliche Download-URL zurück.
     *
     * @param imageBytes Inhalt der Bilddatei, kommt aus dem Bild-Picker
     */
    suspend fun uploadMenuItemImage(restaurantId: String, imageBytes: ByteArray): Result<String>

    /** Lädt das Profilbild eines Kontos hoch (ersetzt ein altes) und gibt die Download-URL zurück. */
    suspend fun uploadProfilePhoto(userId: String, imageBytes: ByteArray): Result<String>
}
