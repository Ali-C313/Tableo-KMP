package com.example.tableo.data.repository

import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.model.StaffProfile
import kotlinx.coroutines.flow.Flow

/** Zugriff auf die Personal-Profile. */
interface StaffRepository {

    /** Liefert alle registrierten Personal-Profile eines Restaurants live als Flow. */
    fun getStaffForRestaurant(restaurantId: String): Flow<List<StaffProfile>>

    /** Personal-Profil zu einem Konto, null wenn das Konto kein Mitarbeiter ist. Zieht alte Einträge auf die User-ID um. */
    suspend fun findProfileForUser(userId: String): StaffProfile?

    /** Eigenes Personal-Profil live, damit z.B. der Dienst-Schalter sofort aktuell ist. */
    fun observeProfileForUser(userId: String): Flow<StaffProfile?>

    /** Zieht alle alten Team-Einträge eines Restaurants auf die User-ID als Dokument-ID um. */
    suspend fun migrateRestaurant(restaurantId: String): Result<Unit>

    /** Mitarbeiter meldet sich zum Dienst an oder ab. */
    suspend fun setOnDuty(profileId: String, onDuty: Boolean): Result<Unit>

    /** Gibt einem Mitarbeiter eine Rolle, z.B. Kellner oder Lieferant. */
    suspend fun updateRole(profileId: String, role: StaffRole): Result<Unit>

    /** Nimmt einen Mitarbeiter aus dem Team, das Konto selbst bleibt bestehen. */
    suspend fun removeStaff(profileId: String): Result<Unit>

    /** Neues Profilbild ins Team-Profil dieses Kontos übernehmen. */
    suspend fun updatePhotoForUser(userId: String, photoUrl: String): Result<Unit>

    /** Löscht alle Team-Einträge eines Kontos, z.B. wenn es gelöscht wird. */
    suspend fun removeAllForUser(userId: String): Result<Unit>

    /** Legt ein neues Personal-Profil an, die User-ID wird zur Dokument-ID. */
    suspend fun registerStaffProfile(profile: StaffProfile): Result<Unit>
}