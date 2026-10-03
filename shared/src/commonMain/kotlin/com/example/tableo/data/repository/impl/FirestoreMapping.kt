package com.example.tableo.data.repository.impl

import com.example.tableo.data.model.MenuItem
import com.example.tableo.data.model.Order
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.model.StaffProfile
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.QuerySnapshot

// Die Dokument-ID steht nicht in den Daten selbst, deshalb wird sie nach dem Lesen dazugesetzt.
// Kaputte Dokumente (z.B. alte Testdaten) werden übersprungen statt die ganze Liste abstürzen zu lassen.

fun QuerySnapshot.toOrders(): List<Order> =
    documents.mapNotNull { doc -> runCatching { doc.data<Order>().copy(id = doc.id) }.getOrNull() }

fun DocumentSnapshot.toOrderOrNull(): Order? =
    if (exists) runCatching { data<Order>().copy(id = id) }.getOrNull() else null

fun QuerySnapshot.toRestaurants(): List<Restaurant> =
    documents.mapNotNull { doc -> runCatching { doc.data<Restaurant>().copy(id = doc.id) }.getOrNull() }

fun QuerySnapshot.toMenuItems(): List<MenuItem> =
    documents.mapNotNull { doc -> runCatching { doc.data<MenuItem>().copy(id = doc.id) }.getOrNull() }

fun QuerySnapshot.toStaffProfiles(): List<StaffProfile> =
    documents.mapNotNull { doc -> runCatching { doc.data<StaffProfile>().copy(id = doc.id) }.getOrNull() }
