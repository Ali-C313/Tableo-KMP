package com.example.tableo.data.enums

/** Bezahlarten im Warenkorb. Es wird noch nicht echt bezahlt, die Auswahl wird nur mit der Bestellung gespeichert. */
enum class PaymentMethod(val label: String) {
    BAR("Barzahlung"),
    PAYPAL("PayPal"),
    APPLE_PAY("Apple Pay"),
    GOOGLE_PAY("Google Pay"),
    KLARNA("Klarna"),
    KREDITKARTE("Kreditkarte")
}
