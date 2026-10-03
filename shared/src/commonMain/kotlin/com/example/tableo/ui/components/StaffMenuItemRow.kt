package com.example.tableo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.tableo.data.model.MenuItem
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing
import com.example.tableo.ui.util.asEuro

/**
 * Eine Zeile in der Speisekarten-Verwaltung fürs Personal mit Foto-Vorschau,
 * Verfügbarkeits-Schalter und Lösch-Button (mit Sicherheitsabfrage).
 *
 * @param onDeleteConfirmed Wird erst aufgerufen, nachdem der Bestätigungs-Dialog bejaht wurde
 */
@Composable
fun StaffMenuItemRow(
    item: MenuItem,
    onEditClick: () -> Unit,
    onAvailabilityChange: (Boolean) -> Unit,
    onDeleteConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

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
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S + AppSpacing.XS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(AppShape.Small)
            ) {
                if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ImagePlaceholder()
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onEditClick)
            ) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${item.category} · ${item.price.asEuro()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = item.available,
                onCheckedChange = onAvailabilityChange
            )
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Artikel löschen",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Artikel löschen?") },
            text = { Text("\"${item.name}\" wird endgültig aus der Speisekarte entfernt.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteConfirmed()
                }) {
                    Text("Löschen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Abbrechen") }
            }
        )
    }
}

@Preview
@Composable
private fun StaffMenuItemRowPreview() {
    StaffMenuItemRow(
        item = MenuItem(),
        onEditClick = {},
        onAvailabilityChange = {},
        onDeleteConfirmed = {}
    )
}