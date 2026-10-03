package com.example.tableo.data.repository.impl

import com.example.tableo.data.repository.StorageRepository
import com.example.tableo.ui.util.currentTimeMillis
import dev.gitlive.firebase.storage.FirebaseStorage
import kotlin.random.Random

/** Lädt Fotos zu Firebase Storage hoch. */
class StorageRepositoryImpl(
    private val storage: FirebaseStorage
) : StorageRepository {

    override suspend fun uploadMenuItemImage(restaurantId: String, imageBytes: ByteArray): Result<String> = runCatching {
        // eigener Dateiname pro Upload, damit sich zwei Bilder nie überschreiben
        val fileName = "${currentTimeMillis()}-${Random.nextInt(100_000)}.jpg"
        val ref = storage.reference.child("menuItems/$restaurantId/$fileName")
        ref.putData(imageBytes.toStorageData())
        ref.getDownloadUrl()
    }

    override suspend fun uploadProfilePhoto(userId: String, imageBytes: ByteArray): Result<String> = runCatching {
        // ein Bild pro Konto, ein neues überschreibt das alte
        val ref = storage.reference.child("profilePhotos/$userId.jpg")
        ref.putData(imageBytes.toStorageData())
        ref.getDownloadUrl()
    }
}
