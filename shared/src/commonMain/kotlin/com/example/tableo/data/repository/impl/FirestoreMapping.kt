package com.example.tableo.data.repository.impl

import com.example.tableo.data.model.MenuItem
import com.example.tableo.data.model.Order
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.model.StaffProfile
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.QuerySnapshot

// Die Dokument-ID steht nicht in den Daten selbst, deshalb wird sie nach dem Lesen dazugesetzt.
// Kaputte Dokumente (z.B. alte Testdaten) werden übersprungen statt die ganze Liste abstürzen zu lassen.

/** Alle Bestellungen aus einer Abfrage. */
fun QuerySnapshot.toOrders(): List<Order> =
    documents.mapNotNull { doc -> runCatching { doc.data<Order>().copy(id = doc.id) }.getOrNull() }

/** Eine Bestellung, null wenn es sie nicht gibt. */
fun DocumentSnapshot.toOrderOrNull(): Order? =
    if (exists) runCatching { data<Order>().copy(id = id) }.getOrNull() else null

/** Alle Restaurants aus einer Abfrage. */
fun QuerySnapshot.toRestaurants(): List<Restaurant> =
    documents.mapNotNull { doc -> runCatching { doc.data<Restaurant>().copy(id = doc.id) }.getOrNull() }

/** Alle Artikel einer Speisekarte aus einer Abfrage. */
fun QuerySnapshot.toMenuItems(): List<MenuItem> =
    documents.mapNotNull { doc -> runCatching { doc.data<MenuItem>().copy(id = doc.id) }.getOrNull() }

/** Alle Team-Einträge aus einer Abfrage. */
fun QuerySnapshot.toStaffProfiles(): List<StaffProfile> =
    documents.mapNotNull { doc -> runCatching { doc.data<StaffProfile>().copy(id = doc.id) }.getOrNull() }

/** Ein Team-Eintrag, null wenn es ihn nicht gibt. */
fun DocumentSnapshot.toStaffProfileOrNull(): StaffProfile? =
    if (exists) runCatching { data<StaffProfile>().copy(id = id) }.getOrNull() else null
