package com.example.tableo.data.repository

/** Zugriff auf die Benutzer-Anmeldung. */
interface AuthRepository {

    /** ID des aktuell eingeloggten Kunden, oder null wenn niemand eingeloggt ist. */
    fun currentUserId(): String?

    /** E-Mail-Adresse des aktuell eingeloggten Kunden, oder null. */
    fun currentUserEmail(): String?

    /** Profilbild des aktuell eingeloggten Nutzers, oder null. */
    fun currentUserPhotoUrl(): String?

    /** Speichert die URL des Profilbilds am Konto. */
    suspend fun updatePhotoUrl(photoUrl: String): Result<Unit>

    /** Anzeigename des aktuell eingeloggten Kunden, oder null. */
    fun currentUserName(): String?

    /** Legt ein neues Kunden-Konto an. */
    suspend fun register(email: String, password: String): Result<Unit>

    /** Meldet einen Kunden mit bestehendem Konto an. */
    suspend fun login(email: String, password: String): Result<Unit>

    /** Ändert den Anzeigenamen des aktuell eingeloggten Kunden. */
    suspend fun updateDisplayName(name: String): Result<Unit>

    /** Meldet den Nutzer mit seinem aktuellen Passwort erneut an. Firebase verlangt das
     *  vor sicherheitsrelevanten Änderungen wie E-Mail oder Passwort, wenn der letzte
     *  Login schon etwas her ist. */
    suspend fun reauthenticate(currentPassword: String): Result<Unit>

    /** Ändert die E-Mail-Adresse des aktuell eingeloggten Kunden. */
    suspend fun updateEmail(newEmail: String): Result<Unit>

    /** Ändert das Passwort des aktuell eingeloggten Kunden. */
    suspend fun updatePassword(newPassword: String): Result<Unit>

    /** Schickt eine E-Mail zum Zurücksetzen des Passworts an die angegebene Adresse. */
    suspend fun sendPasswordReset(email: String): Result<Unit>

    /** Meldet den aktuellen Kunden ab. */
    suspend fun logout()
}