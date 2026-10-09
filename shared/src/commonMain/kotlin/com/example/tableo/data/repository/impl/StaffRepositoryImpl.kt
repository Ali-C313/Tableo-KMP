package com.example.tableo.data.repository.impl

import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.remote.FirestoreCollections
import com.example.tableo.data.repository.StaffRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Zugriff auf die Personal-Profile in Firestore. Die Dokument-ID ist die User-ID des Kontos,
 * so können die Firestore-Regeln direkt prüfen, wo jemand arbeitet und welche Rolle er hat.
 */
class StaffRepositoryImpl(
    firestore: FirebaseFirestore
) : StaffRepository {

    private val staffCollection = firestore.collection(FirestoreCollections.STAFF_PROFILES)

    override fun getStaffForRestaurant(restaurantId: String): Flow<List<StaffProfile>> =
        staffCollection
            .where { "restaurantId" equalTo restaurantId }
            .snapshots
            .map { it.toStaffProfiles() }

    override suspend fun findProfileForUser(userId: String): StaffProfile? {
        staffCollection.document(userId).get().toStaffProfileOrNull()?.let { return it }
        // alter Eintrag mit zufälliger ID: auf die User-ID umziehen
        val old = staffCollection.where { "userId" equalTo userId }.limit(1).get().toStaffProfiles().firstOrNull()
            ?: return null
        return runCatching { moveToUserId(old) }.getOrDefault(old)
    }

    override fun observeProfileForUser(userId: String): Flow<StaffProfile?> = flow {
        // zieht einen alten Eintrag vorher um, damit das Dokument unten existiert
        runCatching { findProfileForUser(userId) }
        emitAll(staffCollection.document(userId).snapshots.map { it.toStaffProfileOrNull() })
    }

    override suspend fun migrateRestaurant(restaurantId: String): Result<Unit> = runCatching {
        staffCollection.where { "restaurantId" equalTo restaurantId }.get().toStaffProfiles()
            .filter { it.userId.isNotBlank() && it.id != it.userId }
            .forEach { moveToUserId(it) }
    }

    override suspend fun setOnDuty(profileId: String, onDuty: Boolean): Result<Unit> = runCatching {
        staffCollection.document(profileId).update("onDuty" to onDuty)
    }

    override suspend fun updateRole(profileId: String, role: StaffRole): Result<Unit> = runCatching {
        staffCollection.document(profileId).update("role" to role.name)
    }

    override suspend fun removeStaff(profileId: String): Result<Unit> = runCatching {
        staffCollection.document(profileId).delete()
    }

    override suspend fun updatePhotoForUser(userId: String, photoUrl: String): Result<Unit> = runCatching {
        staffCollection.document(userId).update("photoUrl" to photoUrl)
    }

    override suspend fun removeAllForUser(userId: String): Result<Unit> = runCatching {
        staffCollection.document(userId).delete()
        // alte Einträge mit zufälliger ID, falls noch welche übrig sind
        staffCollection.where { "userId" equalTo userId }.get().documents.forEach { doc ->
            runCatching { staffCollection.document(doc.id).delete() }
        }
    }

    override suspend fun registerStaffProfile(profile: StaffProfile): Result<Unit> = runCatching {
        staffCollection.document(profile.userId).set(profile.copy(id = profile.userId))
    }

    /** Kopiert ein Profil auf die User-ID als Dokument-ID und löscht den alten Eintrag. */
    private suspend fun moveToUserId(profile: StaffProfile): StaffProfile {
        val moved = profile.copy(id = profile.userId)
        staffCollection.document(profile.userId).set(moved)
        staffCollection.document(profile.id).delete()
        return moved
    }
}
