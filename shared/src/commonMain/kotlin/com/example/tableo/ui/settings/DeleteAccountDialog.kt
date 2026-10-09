package com.example.tableo.ui.settings

import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import com.example.tableo.ui.components.PasswordTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.resources.Res
import com.example.tableo.resources.common_cancel
import com.example.tableo.resources.settings_delete_account
import com.example.tableo.resources.settings_delete_password
import com.example.tableo.resources.settings_delete_text
import com.example.tableo.resources.settings_delete_title
import com.example.tableo.resources.settings_deleting
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Sicherheitsabfrage mit Passwort, bevor das Konto endgültig gelöscht wird. */
@Composable
fun DeleteAccountDialog(
    errorMessage: String?,
    isDeleting: Boolean,
    onConfirm: (password: String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = { Text(stringResource(Res.string.settings_delete_title)) },
        text = {
            Column {
                Text(
                    stringResource(Res.string.settings_delete_text),
                    style = MaterialTheme.typography.bodyMedium
                )
                PasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(Res.string.settings_delete_password),
                    isError = errorMessage != null,
                    onDone = { if (password.isNotBlank() && !isDeleting) onConfirm(password) },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                if (errorMessage != null) {
                    Text(
                        errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(password) },
                enabled = password.isNotBlank() && !isDeleting
            ) {
                Text(if (isDeleting) stringResource(Res.string.settings_deleting) else stringResource(Res.string.settings_delete_account), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) { Text(stringResource(Res.string.common_cancel)) }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteAccountDialogPreview() {
    TableoTheme {
        DeleteAccountDialog(errorMessage = null, isDeleting = false, onConfirm = {}, onDismiss = {})
    }
}
