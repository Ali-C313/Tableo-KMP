package com.example.tableo.ui.components

import com.example.tableo.resources.Res
import com.example.tableo.resources.address_placeholder
import com.example.tableo.resources.address_searching
import com.example.tableo.resources.address_title
import com.example.tableo.resources.address_use_location
import com.example.tableo.resources.common_cancel
import com.example.tableo.resources.guest_apply
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.util.rememberLocationPermissionRequest

/**
 * Dialog zum Eingeben der Lieferadresse oder zum Teilen des eigenen Standorts.
 * Die Standort-Berechtigung wird erst beim Antippen von "Meinen Standort verwenden" abgefragt.
 *
 * @param currentAddress Bisher gewählte Adresse, steht dann schon im Textfeld
 * @param isLoading true, während gesucht oder der Standort ermittelt wird
 * @param errorMessage Fehlertext unter dem Feld, null wenn alles ok
 * @param onSearch Sucht die eingetippte Adresse
 * @param onUseLocation Wird nach erteilter Berechtigung aufgerufen
 * @param onPermissionDenied Wird aufgerufen, wenn der Standort abgelehnt wurde
 */
@Composable
fun AddressDialog(
    currentAddress: String?,
    isLoading: Boolean,
    errorMessage: String?,
    onSearch: (String) -> Unit,
    onUseLocation: () -> Unit,
    onPermissionDenied: () -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf(currentAddress.orEmpty()) }

    val requestLocation = rememberLocationPermissionRequest { granted ->
        if (granted) onUseLocation() else onPermissionDenied()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.address_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text(stringResource(Res.string.address_placeholder)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = requestLocation,
                    enabled = !isLoading,
                    shape = AppShape.Small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(stringResource(Res.string.address_use_location), modifier = Modifier.padding(start = 8.dp))
                }

                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text(
                            stringResource(Res.string.address_searching),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSearch(query) },
                enabled = query.isNotBlank() && !isLoading
            ) { Text(stringResource(Res.string.guest_apply)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.common_cancel)) }
        }
    )
}

@Preview
@Composable
private fun AddressDialogPreview() {
    TableoTheme {
        AddressDialog(
            currentAddress = "Reeperbahn 12, Hamburg",
            isLoading = false,
            errorMessage = null,
            onSearch = {},
            onUseLocation = {},
            onPermissionDenied = {},
            onDismiss = {}
        )
    }
}
