package com.example.tableo.data.enums

import androidx.compose.runtime.Composable
import com.example.tableo.resources.Res
import com.example.tableo.resources.payment_apple_pay
import com.example.tableo.resources.payment_card
import com.example.tableo.resources.payment_cash
import com.example.tableo.resources.payment_google_pay
import com.example.tableo.resources.payment_klarna
import com.example.tableo.resources.payment_paypal
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Bezahlarten im Warenkorb. Es wird noch nicht echt bezahlt, die Auswahl wird nur mit der Bestellung gespeichert. */
enum class PaymentMethod(val labelRes: StringResource) {
    BAR(Res.string.payment_cash),
    PAYPAL(Res.string.payment_paypal),
    APPLE_PAY(Res.string.payment_apple_pay),
    GOOGLE_PAY(Res.string.payment_google_pay),
    KLARNA(Res.string.payment_klarna),
    KREDITKARTE(Res.string.payment_card)
}

/** Übersetzter Name für die Anzeige. */
val PaymentMethod.label: String
    @Composable get() = stringResource(labelRes)
