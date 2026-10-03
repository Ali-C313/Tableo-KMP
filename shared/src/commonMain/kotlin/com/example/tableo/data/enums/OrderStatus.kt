package com.example.tableo.data.enums

/**
 * Status einer Bestellung, wird vom Personal bzw. Fahrer geändert.
 * Am Tisch / Abholung: OFFEN -> IN_ZUBEREITUNG -> FERTIG -> ERLEDIGT.
 * Lieferung: OFFEN -> IN_ZUBEREITUNG -> FERTIG -> ABHOLBEREIT -> ANGENOMMEN -> UNTERWEGS -> ERLEDIGT.
 * Das Personal kann auch direkt auf erledigt springen.
 * STORNIERT geht vom Personal, bis die Bestellung das Restaurant verlassen hat (UNTERWEGS),
 * vom Kunden nur solange sie noch OFFEN ist.
 */
enum class OrderStatus {
    OFFEN,
    IN_ZUBEREITUNG,
    FERTIG,

    /** Personal ist fertig, Lieferung wartet auf einen Fahrer. */
    ABHOLBEREIT,

    /** Ein Fahrer hat die Lieferung in seine Tour genommen, ist aber noch nicht losgefahren. */
    ANGENOMMEN,
    UNTERWEGS,
    ERLEDIGT,
    STORNIERT,

    /** Alter Status von vor der Umstellung auf ERLEDIGT. Bleibt drin, damit alte Bestellungen weiter geladen werden. */
    BEZAHLT;

    /** true, wenn die Bestellung abgeschlossen ist und ins Archiv gehört (auch storniert). */
    val isDone: Boolean
        get() = this == ERLEDIGT || this == BEZAHLT || this == STORNIERT

    /** true, solange das Personal noch stornieren darf. Ist der Fahrer losgefahren, geht es nicht mehr. */
    val canRestaurantCancel: Boolean
        get() = this != UNTERWEGS && !isDone

    /** true, wenn das Personal fertig ist und die Lieferung beim Fahrer liegt. */
    val isWithDriver: Boolean
        get() = this == ABHOLBEREIT || this == ANGENOMMEN || this == UNTERWEGS
}
