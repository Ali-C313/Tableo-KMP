package com.example.tableo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.ui.theme.TableoTheme

/**
 * Eine anklickbare Zeile im Listen-Stil: Icon, Titel, optionaler Untertitel, Symbol rechts.
 * Wird für Listen in Konto und Einstellungen benutzt.
 *
 * @param showChevron false blendet das Symbol rechts aus
 * @param trailingIcon Symbol rechts, Standard ist der Pfeil, z.B. ein Stift für "bearbeiten"
 * @param tint Optionale Farbe für Icon+Titel, z.B. für eine rot hervorgehobene "Abmelden"-Zeile
 */
@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    showChevron: Boolean = true,
    trailingIcon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
    tint: Color? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val contentColor = tint ?: MaterialTheme.colorScheme.onSurface
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint ?: MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = contentColor)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (showChevron) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsRowPreview() {
    TableoTheme {
        Column {
            SettingsRow(
                icon = Icons.Filled.Person,
                title = "Personal-Funktionen",
                subtitle = "Anmelden oder registrieren als Mitarbeiter:in",
                onClick = {}
            )
        }
    }
}