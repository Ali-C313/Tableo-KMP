package com.example.tableo.data.repository

/** Zusatzdaten zum Konto, z.B. ob es zum Löschen vorgemerkt ist. */
interface UserRepository {

    /** Merkt das Konto zum Löschen vor. Es bleibt 30 Tage bestehen. */
    suspend fun requestDeletion(userId: String): Result<Unit>

    /** Zeitpunkt, an dem das Löschen beantragt wurde, null wenn nicht vorgemerkt. */
    suspend fun deletionRequestedAt(userId: String): Long?

    /** Nimmt die Vormerkung zurück oder räumt sie nach dem endgültigen Löschen weg. */
    suspend fun clearDeletion(userId: String): Result<Unit>
}

/** So lange kann man ein gelöschtes Konto durch Anmelden noch zurückholen. */
const val ACCOUNT_DELETION_GRACE_DAYS = 30

const val ACCOUNT_DELETION_GRACE_MILLIS = ACCOUNT_DELETION_GRACE_DAYS * 24L * 60 * 60 * 1000
