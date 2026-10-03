package com.example.tableo.data.enums

/** Aufgabe eines Mitarbeiters im Restaurant, wird im Team-Reiter vergeben. */
enum class StaffRole(val label: String) {
    OHNE("Ohne Rolle"),
    LEITUNG("Leitung"),
    KELLNER("Kellner"),
    KOCH("Koch"),
    KUECHENHILFE("Küchenhilfe"),
    BAR("Bar"),
    LIEFERANT("Lieferant")
}
