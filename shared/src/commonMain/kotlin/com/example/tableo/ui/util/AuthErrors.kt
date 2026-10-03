package com.example.tableo.ui.util

import dev.gitlive.firebase.auth.FirebaseAuthEmailException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.FirebaseAuthRecentLoginRequiredException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseAuthWeakPasswordException

/**
 * Übersetzt technische Firebase-Auth-Fehler in verständliche deutsche Meldungen.
 * Die Fehlerarten sind auf Android und iOS gleich, deshalb wird nach Klasse unterschieden.
 */
fun mapFirebaseAuthError(e: Throwable?): String = when (e) {
    // muss vor InvalidCredentials stehen, weil es davon erbt
    is FirebaseAuthWeakPasswordException -> "Das Passwort muss mindestens 6 Zeichen haben."
    is FirebaseAuthInvalidCredentialsException -> "E-Mail oder Passwort ist falsch."
    is FirebaseAuthInvalidUserException -> "Kein Konto mit dieser E-Mail-Adresse gefunden."
    is FirebaseAuthUserCollisionException -> "Diese E-Mail-Adresse wird bereits verwendet."
    is FirebaseAuthRecentLoginRequiredException -> "Bitte gib zur Sicherheit dein aktuelles Passwort erneut ein."
    is FirebaseAuthEmailException -> "Ungültige E-Mail-Adresse."
    else -> e?.message ?: "Ein Fehler ist aufgetreten."
}
