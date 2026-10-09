package com.example.tableo.data.repository.impl

import com.example.tableo.data.repository.AuthRepository
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth

/** Zugriff auf die Benutzer-Anmeldung über Firebase Authentication (GitLive, Android und iOS). */
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    // anonyme Gäste zählen in der App nicht als eingeloggt
    override fun currentUserId(): String? =
        firebaseAuth.currentUser?.takeUnless { it.isAnonymous }?.uid

    override suspend fun ensureSignedIn() {
        if (firebaseAuth.currentUser == null) {
            runCatching { firebaseAuth.signInAnonymously() }
        }
    }

    override fun currentUserEmail(): String? = firebaseAuth.currentUser?.email

    override fun currentUserName(): String? = firebaseAuth.currentUser?.displayName

    override fun currentUserPhotoUrl(): String? = firebaseAuth.currentUser?.photoURL

    override suspend fun updatePhotoUrl(photoUrl: String): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.updateProfile(photoUrl = photoUrl)
    }

    override suspend fun register(email: String, password: String): Result<Unit> = runCatching {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun updateDisplayName(name: String): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.updateProfile(displayName = name)
    }

    override suspend fun updateEmail(newEmail: String): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.verifyBeforeUpdateEmail(newEmail)
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.updatePassword(newPassword)
    }

    override suspend fun reauthenticate(currentPassword: String): Result<Unit> = runCatching {
        val user = firebaseAuth.currentUser ?: throw IllegalStateException("Nicht eingeloggt")
        val email = user.email ?: throw IllegalStateException("Keine E-Mail-Adresse hinterlegt")
        user.reauthenticate(EmailAuthProvider.credential(email, currentPassword))
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email)
    }

    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        val user = firebaseAuth.currentUser ?: throw IllegalStateException("Nicht eingeloggt")
        user.delete()
        ensureSignedIn()
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        ensureSignedIn()
    }
}
