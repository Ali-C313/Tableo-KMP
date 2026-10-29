package com.example.tableo.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.resources.Res
import com.example.tableo.resources.component_password_hide
import com.example.tableo.resources.component_password_show
import org.jetbrains.compose.resources.stringResource

/**
 * Feld für ein Passwort mit Auge zum Ein- und Ausblenden.
 * Enter auf der Tastatur schließt die Tastatur und ruft [onDone] auf, z.B. zum Anmelden.
 *
 * @param onDone Wird nach Enter aufgerufen, Standard ist nur Tastatur zu
 */
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    onDone: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        isError = isError,
        singleLine = true,
        shape = shape,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            onDone()
        }),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (visible) stringResource(Res.string.component_password_hide) else stringResource(Res.string.component_password_show)
                )
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun PasswordTextFieldPreview() {
    TableoTheme {
        PasswordTextField(
            value = "geheim123",
            onValueChange = {},
            label = "Passwort",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
