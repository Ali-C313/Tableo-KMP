package com.example.tableo.data.repository.mock

import com.example.tableo.data.repository.StorageRepository

/** Tut für Previews so, als hätte der Upload geklappt. */
class StorageRepositoryMock : StorageRepository {
    override suspend fun uploadMenuItemImage(restaurantId: String, imageBytes: ByteArray): Result<String> =
        Result.success("")

    override suspend fun uploadProfilePhoto(userId: String, imageBytes: ByteArray): Result<String> =
        Result.success("")

    override suspend fun deleteProfilePhoto(userId: String): Result<Unit> = Result.success(Unit)
}
