package com.example.tableo.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.resources.Res
import com.example.tableo.resources.common_choose
import org.jetbrains.compose.resources.stringResource
import com.example.tableo.ui.theme.AppShape

/**
 * Ein Dropdown mit Button für die Auswahl-Listen der App, z.B. Kategorie oder Währung.
 *
 * @param selected Aktuell gewählter Eintrag, oder null wenn noch nichts gewählt ist
 * @param options Alle Einträge, die im Menü zur Auswahl stehen
 * @param placeholder Text auf dem Button, solange nichts gewählt ist (dient auch als Bildschirmleser-Beschreibung)
 * @param fillWidth true = Button über die volle Breite, false = nur so breit wie sein Inhalt
 * @param optionLabel Wandelt einen Eintrag in den angezeigten Text um, z.B. 5 -> "Tisch 5"
 */
@Composable
fun <T> DropdownPicker(
    selected: T?,
    options: List<T>,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(Res.string.common_choose),
    fillWidth: Boolean = false,
    optionLabel: @Composable (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = AppShape.Small,
            modifier = if (fillWidth) Modifier.fillMaxWidth() else Modifier
        ) {
            Text(
                text = selected?.let { optionLabel(it) } ?: placeholder,
                modifier = if (fillWidth) Modifier.weight(1f) else Modifier
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = placeholder
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DropdownPickerPreview() {
    TableoTheme {
        DropdownPicker(
            selected = "EUR",
            options = listOf("EUR", "USD", "GBP", "TRY"),
            onSelected = {},
            placeholder = "Währung wählen"
        )
    }
}