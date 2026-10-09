package com.example.tableo.ui.settings

import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.resources.Res
import com.example.tableo.resources.common_cancel
import com.example.tableo.resources.settings_as_driver
import com.example.tableo.resources.settings_in_team
import com.example.tableo.resources.settings_link_confirm
import com.example.tableo.resources.settings_link_text
import com.example.tableo.resources.settings_link_title
import com.example.tableo.resources.settings_restaurant_id
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Dialog zum Eintragen der Restaurant-ID, die der Chef im Team-Reiter sieht. */
@Composable
fun LinkRestaurantDialog(
    errorMessage: String?,
    onConfirm: (restaurantId: String, asDriver: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var restaurantId by remember { mutableStateOf("") }
    var isDriver by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_link_title)) },
        text = {
            Column {
                Text(
                    stringResource(Res.string.settings_link_text),
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = restaurantId,
                    onValueChange = { restaurantId = it },
                    label = { Text(stringResource(Res.string.settings_restaurant_id)) },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    FilterChip(selected = !isDriver, onClick = { isDriver = false }, label = { Text(stringResource(Res.string.settings_in_team)) })
                    Spacer(Modifier.width(8.dp))
                    FilterChip(selected = isDriver, onClick = { isDriver = true }, label = { Text(stringResource(Res.string.settings_as_driver)) })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(restaurantId, isDriver) }, enabled = restaurantId.isNotBlank()) {
                Text(stringResource(Res.string.settings_link_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.common_cancel)) }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun LinkRestaurantDialogPreview() {
    TableoTheme {
        LinkRestaurantDialog(errorMessage = null, onConfirm = { _, _ -> }, onDismiss = {})
    }
}
