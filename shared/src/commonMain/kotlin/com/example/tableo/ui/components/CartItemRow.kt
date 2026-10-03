package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.model.OrderItem
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing
import com.example.tableo.ui.util.asEuro

/**
 * Eine Zeile im Warenkorb mit Mengensteller.
 *
 * @param onIncrement Wird aufgerufen, wenn "+" angetippt wird
 * @param onDecrement Wird aufgerufen, wenn "−" angetippt wird
 */
@Composable
fun CartItemRow(
    orderItem: OrderItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.M - 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    orderItem.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${orderItem.pricePerItem.asEuro()} je Stück",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StepperButton(label = "−", onClick = onDecrement)
                Text(
                    text = "${orderItem.quantity}",
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                StepperButton(
                    label = "+",
                    onClick = onIncrement
                )
            }

            Text(
                text = orderItem.subtotal.asEuro(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = AppSpacing.S + AppSpacing.XS)
                    .width(72.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

/** Kleiner runder +/− Button für Mengensteller (z.B. im Warenkorb). */
@Composable
private fun StepperButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(AppShape.Small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemRowPreview() {
    TableoTheme {
        CartItemRow(
            orderItem = OrderItem(
                menuItemId = "1",
                name = "Doppelapfel",
                pricePerItem = 12.0,
                quantity = 2
            ),
            onIncrement = {},
            onDecrement = {}
        )
    }
}