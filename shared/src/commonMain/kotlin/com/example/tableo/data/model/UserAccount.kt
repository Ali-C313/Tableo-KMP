package com.example.tableo.data.model

import kotlinx.serialization.Serializable

/**
 * Zusatzdaten zu einem Konto in Firestore, Dokument-ID ist die User-ID.
 *
 * @param deletionRequestedAt Wann das Löschen beantragt wurde (Millisekunden), 0 = nicht beantragt
 */
@Serializable
data class UserAccount(
    val deletionRequestedAt: Long = 0
)
