package com.example.tableo.util

import com.example.tableo.resources.Res
import com.example.tableo.resources.auth_error_email_taken
import com.example.tableo.resources.auth_error_invalid_email
import com.example.tableo.resources.auth_error_no_account
import com.example.tableo.resources.auth_error_recent_login
import com.example.tableo.resources.auth_error_weak_password
import com.example.tableo.resources.auth_error_wrong_credentials
import com.example.tableo.resources.common_error_generic
import dev.gitlive.firebase.auth.FirebaseAuthEmailException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.FirebaseAuthRecentLoginRequiredException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseAuthWeakPasswordException
import org.jetbrains.compose.resources.getString

/** Fehlermeldungen beim Anmelden und Registrieren. */
object AuthErrorHelper {

    /**
     * Übersetzt technische Firebase-Auth-Fehler in verständliche Meldungen in der eingestellten Sprache.
     * Die Fehlerarten sind auf Android und iOS gleich, deshalb wird nach Klasse unterschieden.
     */
    suspend fun message(e: Throwable?): String = when (e) {
        // muss vor InvalidCredentials stehen, weil es davon erbt
        is FirebaseAuthWeakPasswordException -> getString(Res.string.auth_error_weak_password)
        is FirebaseAuthInvalidCredentialsException -> getString(Res.string.auth_error_wrong_credentials)
        is FirebaseAuthInvalidUserException -> getString(Res.string.auth_error_no_account)
        is FirebaseAuthUserCollisionException -> getString(Res.string.auth_error_email_taken)
        is FirebaseAuthRecentLoginRequiredException -> getString(Res.string.auth_error_recent_login)
        is FirebaseAuthEmailException -> getString(Res.string.auth_error_invalid_email)
        else -> e?.message ?: getString(Res.string.common_error_generic)
    }
}
