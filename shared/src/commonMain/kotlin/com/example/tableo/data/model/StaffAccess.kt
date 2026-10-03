package com.example.tableo.data.model

import com.example.tableo.data.enums.StaffRole

/**
 * Restaurant und Profil eines Mitarbeiters. Lieferanten landen im Fahrer-Bereich, alle anderen im Dashboard.
 * Die Leitung darf beides öffnen, z.B. wenn sie selbst mal ausliefert.
 */
data class StaffAccess(
    val restaurant: Restaurant,
    val profile: StaffProfile
) {
    val isDriver: Boolean get() = profile.role == StaffRole.LIEFERANT

    val canOpenDashboard: Boolean get() = !isDriver

    val canOpenDriverArea: Boolean get() = isDriver || profile.role == StaffRole.LEITUNG
}
