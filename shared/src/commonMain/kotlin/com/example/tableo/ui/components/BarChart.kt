package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.model.ChartBucket
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.util.asEuro

/**
 * Einfaches Balkendiagramm ohne externe Bibliothek. Jeder Balken ist eine farbige Box
 * mit Höhe proportional zum größten Wert, Beschriftung darunter.
 */
@Composable
fun BarChart(
    buckets: List<ChartBucket>,
    modifier: Modifier = Modifier
) {
    val maxRevenue = buckets.maxOfOrNull { it.revenue }?.coerceAtLeast(1.0) ?: 1.0
    val maxBarHeight = 120.dp

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        buckets.forEach { bucket ->
            val barHeightFraction = (bucket.revenue / maxRevenue).toFloat().coerceIn(0f, 1f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                if (bucket.revenue > 0) {
                    Text(
                        text = bucket.revenue.asEuro(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(22.dp)
                        .height(maxBarHeight * barHeightFraction + 4.dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = bucket.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun BarChartPreview() {
    TableoTheme {
        BarChart(
            buckets = listOf(
                ChartBucket("Mo", 45.0, 4),
                ChartBucket("Di", 80.0, 7),
                ChartBucket("Mi", 30.0, 3),
                ChartBucket("Do", 95.0, 9),
                ChartBucket("Fr", 60.0, 5),
                ChartBucket("Sa", 20.0, 2)
            )
        )
    }
}