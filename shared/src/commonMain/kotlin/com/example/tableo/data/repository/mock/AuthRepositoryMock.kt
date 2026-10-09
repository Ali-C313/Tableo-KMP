package com.example.tableo.data.repository.mock

import com.example.tableo.data.repository.AuthRepository

/** Fake-Login für Previews. */
class AuthRepositoryMock(
    private var loggedIn: Boolean = true,
    private var email: String? = "ali@example.com",
    private var name: String? = "Ali",
    private var photoUrl: String? = null
) : AuthRepository {

    override fun currentUserId(): String? = if (loggedIn) "mock-user" else null
    override suspend fun ensureSignedIn() = Unit
    override fun currentUserEmail(): String? = email
    override fun currentUserName(): String? = name
    override fun currentUserPhotoUrl(): String? = photoUrl

    override suspend fun updatePhotoUrl(photoUrl: String): Result<Unit> = runCatching {
        this.photoUrl = photoUrl
    }

    override suspend fun register(email: String, password: String): Result<Unit> = runCatching {
        loggedIn = true
        this.email = email
    }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        loggedIn = true
        this.email = email
    }

    override suspend fun updateDisplayName(name: String): Result<Unit> = runCatching {
        this.name = name
    }

    override suspend fun updateEmail(newEmail: String): Result<Unit> = runCatching {
        this.email = newEmail
    }

    override suspend fun reauthenticate(currentPassword: String): Result<Unit> = Result.success(Unit)

    override suspend fun updatePassword(newPassword: String): Result<Unit> = runCatching { }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = Result.success(Unit)

    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        loggedIn = false
        email = null
        name = null
    }

    override suspend fun logout() {
        loggedIn = false
    }
}