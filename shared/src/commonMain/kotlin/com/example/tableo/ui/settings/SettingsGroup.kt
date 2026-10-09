package com.example.tableo.ui.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.ui.theme.AppShape
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.components.SettingsRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

/** Mehrere Zeilen in einer abgerundeten Karte, wie in den iPhone-Einstellungen. */
@Composable
fun SettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp), content = content)
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsGroupPreview() {
    TableoTheme {
        SettingsGroup {
            SettingsRow(icon = Icons.Filled.Person, title = "Persönliche Daten", onClick = {})
            GroupDivider()
            SettingsRow(icon = Icons.Filled.Person, title = "Meine Bestellungen", onClick = {})
        }
    }
}
