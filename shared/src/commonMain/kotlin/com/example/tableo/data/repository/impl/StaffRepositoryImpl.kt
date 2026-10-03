package com.example.tableo.data.repository.impl

import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.remote.FirestoreCollections
import com.example.tableo.data.repository.StaffRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Zugriff auf die Personal-Profile in Firestore. */
class StaffRepositoryImpl(
    firestore: FirebaseFirestore
) : StaffRepository {

    private val staffCollection = firestore.collection(FirestoreCollections.STAFF_PROFILES)

    override fun getStaffForRestaurant(restaurantId: String): Flow<List<StaffProfile>> =
        staffCollection
            .where { "restaurantId" equalTo restaurantId }
            .snapshots
            .map { it.toStaffProfiles() }

    override suspend fun findProfileForUser(userId: String): StaffProfile? =
        staffCollection.where { "userId" equalTo userId }.limit(1).get().toStaffProfiles().firstOrNull()

    override fun observeProfileForUser(userId: String): Flow<StaffProfile?> =
        staffCollection
            .where { "userId" equalTo userId }
            .snapshots
            .map { it.toStaffProfiles().firstOrNull() }

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
        staffCollection.where { "userId" equalTo userId }.get().documents.forEach { doc ->
            staffCollection.document(doc.id).update("photoUrl" to photoUrl)
        }
    }

    override suspend fun removeAllForUser(userId: String): Result<Unit> = runCatching {
        staffCollection.where { "userId" equalTo userId }.get().documents.forEach { doc ->
            staffCollection.document(doc.id).delete()
        }
    }

    override suspend fun registerStaffProfile(profile: StaffProfile): Result<Unit> = runCatching {
        staffCollection.add(profile)
    }
}
