package com.example.tableo.data.model

/** Ein einzelner Zeitabschnitt im Diagramm, z.B. ein Tag oder eine Woche. */
data class ChartBucket(
    val label: String,
    val revenue: Double,
    val orderCount: Int
)