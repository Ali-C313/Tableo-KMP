package com.example.tableo.data.enums

import androidx.compose.runtime.Composable
import com.example.tableo.resources.Res
import com.example.tableo.resources.period_24h
import com.example.tableo.resources.period_3d
import com.example.tableo.resources.period_6_months
import com.example.tableo.resources.period_month
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Auswählbare Zeiträume fürs Statistik-Diagramm. */
enum class AnalyticsPeriod(val labelRes: StringResource, val millis: Long, val bucketCount: Int) {
    LAST_24H(Res.string.period_24h, 24L * 60 * 60 * 1000, 6),
    LAST_3D(Res.string.period_3d, 3L * 24 * 60 * 60 * 1000, 3),
    LAST_MONTH(Res.string.period_month, 30L * 24 * 60 * 60 * 1000, 6),
    LAST_6_MONTHS(Res.string.period_6_months, 182L * 24 * 60 * 60 * 1000, 6)
}

/** Übersetzter Name für die Anzeige. */
val AnalyticsPeriod.label: String
    @Composable get() = stringResource(labelRes)
