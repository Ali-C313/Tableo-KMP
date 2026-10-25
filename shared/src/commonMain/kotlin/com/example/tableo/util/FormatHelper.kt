package com.example.tableo.util

import kotlin.math.abs
import kotlin.math.roundToLong

/** Formatiert Preise, Zeiten und Namen für die Anzeige. */
object FormatHelper {

    private val currencySymbols = mapOf("USD" to "$", "GBP" to "£", "TRY" to "₺")

    /**
     * Zahl mit festen Nachkommastellen, z.B. 13.5 -> "13,50". String.format gibt es auf iOS nicht,
     * deshalb von Hand gerechnet.
     *
     * @param decimalSeparator "," für deutsch, "." für englisch
     */
    fun decimals(value: Double, decimals: Int, decimalSeparator: String = ","): String {
        var factor = 1L
        repeat(decimals) { factor *= 10 }
        val rounded = (abs(value) * factor).roundToLong()
        val whole = rounded / factor
        val fraction = (rounded % factor).toString().padStart(decimals, '0')
        val sign = if (value < 0 && rounded != 0L) "-" else ""
        return if (decimals == 0) "$sign$whole" else "$sign$whole$decimalSeparator$fraction"
    }

    /** Preis in Euro, z.B. 13.0 -> "13,00 €". */
    fun euro(price: Double): String = "${decimals(price, 2)} €"

    /** Betrag mit dem Symbol der Fremdwährung, z.B. "USD" -> "$14.07". */
    fun foreignCurrency(amount: Double, currencyCode: String): String {
        val symbol = currencySymbols[currencyCode] ?: currencyCode
        return "$symbol${decimals(amount, 2, ".")}"
    }

    /** Zeitstempel als Datum und Uhrzeit, z.B. "03.10.2026, 19:30". */
    fun dateTime(millis: Long): String = formatDateTime(millis, "dd.MM.yyyy, HH:mm")

    /** Zweistellig mit führender Null, z.B. 7 -> "07". Für Uhrzeiten. */
    fun twoDigits(number: Int): String = number.toString().padStart(2, '0')

    /** Nur der Vorname, z.B. "Omar Haddad" -> "Omar". Kunden sehen vom Fahrer nur den Vornamen. */
    fun firstName(fullName: String): String = fullName.trim().substringBefore(" ")
}
