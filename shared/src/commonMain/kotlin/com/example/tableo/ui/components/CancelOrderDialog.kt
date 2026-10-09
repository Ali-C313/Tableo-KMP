package com.example.tableo.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.resources.Res
import com.example.tableo.resources.cancel_dialog_confirm
import com.example.tableo.resources.cancel_dialog_keep
import com.example.tableo.resources.cancel_dialog_title
import org.jetbrains.compose.resources.stringResource

/**
 * Sicherheitsabfrage vor dem Stornieren, damit niemand aus Versehen storniert.
 *
 * @param text Erklärung unter der Überschrift, z.B. für wen die Bestellung war
 */
@Composable
fun CancelOrderDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.cancel_dialog_title)) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(stringResource(Res.string.cancel_dialog_confirm), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel_dialog_keep)) }
        }
    )
}

@Preview
@Composable
private fun CancelOrderDialogPreview() {
    TableoTheme {
        CancelOrderDialog(
            text = "Das kann nicht rückgängig gemacht werden.",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
