package com.example.tableo.navigation

import kotlinx.serialization.Serializable

/** Alle Ziele der Navigation. */
@Serializable
sealed interface Route {

    /** Ladebildschirm beim Start. */
    @Serializable
    data object Splash : Route

    /** Einführung beim ersten Start. */
    @Serializable
    data object Onboarding : Route

    /** Startseite mit allen Restaurants. */
    @Serializable
    data object RestaurantList : Route

    /** Speisekarte eines Restaurants. */
    @Serializable
    data class GuestMenu(
        val restaurantId: String,
        val restaurantName: String
    ) : Route

    /** Tisch oder Abholung auswählen. */
    @Serializable
    data class OrderDetails(
        val restaurantId: String,
        val restaurantName: String
    ) : Route

    /** Warenkorb. */
    @Serializable
    data class Cart(
        val restaurantId: String,
        val restaurantName: String,
        // als Text statt Enum: auf iOS kann die Navigation Enums nicht automatisch umwandeln
        val orderType: String,
        val tableNumber: Int? = null,
        val pickupTime: String? = null
    ) : Route

    /** Bestätigung nach dem Absenden. */
    @Serializable
    data class OrderConfirmation(
        val restaurantName: String
    ) : Route

    /** Alle Details einer Bestellung für den Kunden. */
    @Serializable
    data class MyOrderDetail(
        val orderId: String
    ) : Route

    /** Live-Tracking einer Lieferung. */
    @Serializable
    data class OrderTracking(
        val orderId: String
    ) : Route

    // ab hier: Bereiche für Mitarbeiter und Fahrer

    /** Dashboard mit offenen Bestellungen. */
    @Serializable
    data class StaffDashboard(
        val restaurantId: String,
        val restaurantName: String
    ) : Route

    /** Abgeschlossene Bestellungen. */
    @Serializable
    data class StaffArchive(
        val restaurantId: String,
        val restaurantName: String
    ) : Route

    /** Speisekarte verwalten. */
    @Serializable
    data class StaffMenuManagement(
        val restaurantId: String,
        val restaurantName: String
    ) : Route

    /** Artikel bearbeiten. Ohne menuItemId wird ein neuer angelegt. */
    @Serializable
    data class StaffMenuItemEdit(
        val restaurantId: String,
        val menuItemId: String? = null
    ) : Route

    /** Team eines Restaurants. */
    @Serializable
    data class StaffTeam(
        val restaurantId: String,
        val restaurantName: String
    ) : Route

    /** Umsatz-Statistik. */
    @Serializable
    data class StaffAnalytics(
        val restaurantId: String,
        val restaurantName: String
    ) : Route

    /** Lieferungen eines Restaurants für die Fahrer. Der Name ordnet die Tour dem Fahrer zu. */
    @Serializable
    data class DriverOrders(
        val restaurantId: String,
        val restaurantName: String,
        val driverName: String
    ) : Route

    /** Kunden-Login und Registrierung. */
    @Serializable
    data object CustomerAuth : Route

    /** Bestellhistorie des Kunden. */
    @Serializable
    data object MyOrders : Route

    /** Konto-Übersicht. */
    @Serializable
    data object Account : Route

    /** Einstellungen. */
    @Serializable
    data object Settings : Route

    /** Impressum, Datenschutz oder AGB. page ist der Name aus LegalPage (iOS kann keine Enums in Routen). */
    @Serializable
    data class Legal(val page: String) : Route
}