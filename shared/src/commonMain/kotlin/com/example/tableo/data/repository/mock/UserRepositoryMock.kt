package com.example.tableo.data.repository.mock

import com.example.tableo.data.repository.UserRepository
import com.example.tableo.util.currentTimeMillis

/** Lösch-Vormerkung für Previews, nur im Speicher. */
class UserRepositoryMock(
    private var requestedAt: Long? = null
) : UserRepository {
    override suspend fun requestDeletion(userId: String): Result<Unit> = runCatching {
        requestedAt = currentTimeMillis()
    }

    override suspend fun deletionRequestedAt(userId: String): Long? = requestedAt

    override suspend fun clearDeletion(userId: String): Result<Unit> = runCatching {
        requestedAt = null
    }
}
