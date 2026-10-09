package com.example.tableo.ui.settings

import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language

/** Zeile mit Icon und Titel links, Auswahl rechts. */
@Composable
fun PickerRow(
    icon: ImageVector,
    title: String,
    picker: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        picker()
    }
}

@Preview(showBackground = true)
@Composable
private fun PickerRowPreview() {
    TableoTheme {
        PickerRow(icon = Icons.Filled.Language, title = "Sprache") {
            Text("Deutsch")
        }
    }
}
