package com.example.tableo.util

import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Zahl mit festen Nachkommastellen, z.B. 13.5 -> "13,50". String.format gibt es auf iOS nicht,
 * deshalb von Hand gerechnet.
 *
 * @param decimalSeparator "," für deutsch, "." für englisch
 */
fun Double.formatDecimals(decimals: Int, decimalSeparator: String = ","): String {
    var factor = 1L
    repeat(decimals) { factor *= 10 }
    val rounded = (abs(this) * factor).roundToLong()
    val whole = rounded / factor
    val fraction = (rounded % factor).toString().padStart(decimals, '0')
    val sign = if (this < 0 && rounded != 0L) "-" else ""
    return if (decimals == 0) "$sign$whole" else "$sign$whole$decimalSeparator$fraction"
}

/** Formatiert einen Preis als deutsche Währungsangabe, z.B. 13.0 -> "13,00 €". */
fun Double.asEuro(): String = "${formatDecimals(2)} €"

private val CurrencySymbols = mapOf("USD" to "$", "GBP" to "£", "TRY" to "₺")

/** Formatiert einen Betrag mit dem Symbol der angegebenen Fremdwährung, z.B. "USD" -> "$14.07". */
fun Double.asForeignCurrency(currencyCode: String): String {
    val symbol = CurrencySymbols[currencyCode] ?: currencyCode
    return "$symbol${formatDecimals(2, ".")}"
}

/** Formatiert einen Zeitstempel (Millisekunden seit Epoch) als deutsches Datum + Uhrzeit. */
fun Long.asDateTime(): String = formatDateTime(this, "dd.MM.yyyy, HH:mm")

/** Zweistellig mit führender Null, z.B. 7 -> "07". Für Uhrzeiten. */
fun Int.twoDigits(): String = toString().padStart(2, '0')

/** Nur der Vorname, z.B. "Omar Haddad" -> "Omar". Kunden sehen vom Fahrer nur den Vornamen. */
fun String.firstName(): String = trim().substringBefore(" ")
