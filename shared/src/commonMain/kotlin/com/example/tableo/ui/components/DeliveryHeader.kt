package com.example.tableo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppShape

/**
 * Kopfzeile der Startseite wie bei Lieferando: links die Adresse, rechts Abholung oder Lieferung.
 *
 * @param address Gewählte Adresse, null wenn noch keine eingegeben wurde
 * @param onAddressClick Öffnet den Adress-Dialog
 */
@Composable
fun DeliveryHeader(
    address: String?,
    deliveryMode: DeliveryMode,
    onAddressClick: () -> Unit,
    onModeSelected: (DeliveryMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onAddressClick)
                .padding(vertical = 6.dp)
        ) {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = address ?: "Adresse eingeben",
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .weight(1f, fill = false)
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Adresse ändern",
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            DeliveryMode.entries.forEach { mode ->
                FilterChip(
                    selected = mode == deliveryMode,
                    onClick = { onModeSelected(mode) },
                    label = { Text(mode.label) },
                    shape = AppShape.Large,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeliveryHeaderPreview() {
    TableoTheme {
        DeliveryHeader(
            address = "Schanzenstraße 5, 20357 Hamburg",
            deliveryMode = DeliveryMode.LIEFERUNG,
            onAddressClick = {},
            onModeSelected = {}
        )
    }
}
