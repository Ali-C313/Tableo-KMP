package com.example.tableo.data.enums

import androidx.compose.runtime.Composable
import com.example.tableo.resources.Res
import com.example.tableo.resources.delivery_mode_delivery
import com.example.tableo.resources.delivery_mode_pickup
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Auswahl oben auf der Startseite, wie bei Lieferando. */
enum class DeliveryMode(val labelRes: StringResource) {
    ABHOLUNG(Res.string.delivery_mode_pickup),
    LIEFERUNG(Res.string.delivery_mode_delivery)
}

/** Übersetzter Name für die Anzeige. */
val DeliveryMode.label: String
    @Composable get() = stringResource(labelRes)
