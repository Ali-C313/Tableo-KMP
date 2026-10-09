package com.example.tableo.data.repository.mock

import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.repository.StaffRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/** Testdaten für Previews, ohne Firestore. */
class StaffRepositoryMock : StaffRepository {

    private val staff = MutableStateFlow(
        listOf(
            StaffProfile(id = "1", restaurantId = "1", userId = "mock-user", name = "Ali", email = "ali@example.com", role = StaffRole.LEITUNG, onDuty = true),
            StaffProfile(id = "2", restaurantId = "1", userId = "u2", name = "Mona", email = "mona@example.com", role = StaffRole.KELLNER, onDuty = true),
            StaffProfile(id = "3", restaurantId = "1", userId = "u3", name = "Omar Haddad", role = StaffRole.LIEFERANT)
        )
    )

    override fun getStaffForRestaurant(restaurantId: String) =
        staff.asStateFlow()

    override suspend fun findProfileForUser(userId: String): StaffProfile? =
        staff.value.find { it.userId == userId }

    override fun observeProfileForUser(userId: String) =
        staff.map { list -> list.find { it.userId == userId } }

    override suspend fun migrateRestaurant(restaurantId: String): Result<Unit> = Result.success(Unit)

    override suspend fun setOnDuty(profileId: String, onDuty: Boolean): Result<Unit> = runCatching {
        staff.value = staff.value.map { if (it.id == profileId) it.copy(onDuty = onDuty) else it }
    }

    override suspend fun updateRole(profileId: String, role: StaffRole): Result<Unit> = runCatching {
        staff.value = staff.value.map { if (it.id == profileId) it.copy(role = role) else it }
    }

    override suspend fun removeStaff(profileId: String): Result<Unit> = runCatching {
        staff.value = staff.value.filter { it.id != profileId }
    }

    override suspend fun updatePhotoForUser(userId: String, photoUrl: String): Result<Unit> = runCatching {
        staff.value = staff.value.map { if (it.userId == userId) it.copy(photoUrl = photoUrl) else it }
    }

    override suspend fun removeAllForUser(userId: String): Result<Unit> = runCatching {
        staff.value = staff.value.filter { it.userId != userId }
    }

    override suspend fun registerStaffProfile(profile: StaffProfile): Result<Unit> = runCatching {
        staff.value = staff.value + profile.copy(id = profile.userId)
    }
}