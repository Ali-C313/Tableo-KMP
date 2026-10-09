package com.example.tableo.ui.guest.orderDetails

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Ein Chip für die Bestellart, ausgewählt in der Hauptfarbe. */
@Composable
fun OrderTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun OrderTypeChipPreview() {
    TableoTheme {
        OrderTypeChip(label = "Am Tisch", selected = true, onClick = {})
    }
}
