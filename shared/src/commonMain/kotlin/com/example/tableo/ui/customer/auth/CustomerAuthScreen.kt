package com.example.tableo.ui.customer.auth

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Switch
import com.example.tableo.data.model.StaffAccess
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.RestaurantRepositoryMock
import com.example.tableo.data.repository.mock.StaffRepositoryMock
import com.example.tableo.data.repository.mock.StorageRepositoryMock
import com.example.tableo.data.repository.mock.UserRepositoryMock
import androidx.compose.material3.AlertDialog
import com.example.tableo.ui.theme.AppShape
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.text.input.ImeAction
import com.example.tableo.ui.components.PasswordTextField
import com.example.tableo.resources.Res
import com.example.tableo.resources.account_name
import com.example.tableo.resources.account_password_min_length
import com.example.tableo.resources.auth_screen_email
import com.example.tableo.resources.auth_screen_email_invalid
import com.example.tableo.resources.auth_screen_forgot_password
import com.example.tableo.resources.auth_screen_have_account
import com.example.tableo.resources.auth_screen_login
import com.example.tableo.resources.auth_screen_new_here
import com.example.tableo.resources.auth_screen_password
import com.example.tableo.resources.auth_screen_register
import com.example.tableo.resources.auth_screen_reset_sent
import com.example.tableo.resources.auth_screen_restaurant_id_hint
import com.example.tableo.resources.auth_screen_restored_text
import com.example.tableo.resources.auth_screen_subtitle
import com.example.tableo.resources.auth_screen_title_register
import com.example.tableo.resources.auth_screen_welcome_back
import com.example.tableo.resources.common_continue
import com.example.tableo.resources.settings_as_driver
import com.example.tableo.resources.settings_in_team
import com.example.tableo.resources.settings_restaurant_id
import com.example.tableo.resources.settings_work_in_restaurant
import org.jetbrains.compose.resources.stringResource

/**
 * Login/Registrierung für Kunden und Mitarbeiter. Toggle zwischen beiden Modi über einen TextButton.
 * Beim Registrieren kann man angeben, dass man in einem Restaurant arbeitet (Restaurant-ID),
 * entweder im Team oder als Fahrer.
 * Prüft E-Mail-Format und Passwort-Länge, bevor überhaupt gesendet wird.
 *
 * @param onAuthSuccess Bekommt bei Mitarbeitern Restaurant und Profil, bei Kunden null
 */
@Composable
fun CustomerAuthScreen(
    onAuthSuccess: (StaffAccess?) -> Unit,
    customerAuthViewModel: CustomerAuthViewModel = koinViewModel()
) {
    val uiState by customerAuthViewModel.uiState.collectAsState()
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isStaff by remember { mutableStateOf(false) }
    var restaurantId by remember { mutableStateOf("") }
    var isDriver by remember { mutableStateOf(false) }

    val isEmailValid = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(email)
    val isPasswordValid = password.length >= 6
    val staffDataValid = !isRegisterMode || !isStaff || restaurantId.isNotBlank()
    val canSubmit = (!isRegisterMode || name.isNotBlank()) && isEmailValid && isPasswordValid &&
            staffDataValid && uiState !is CustomerAuthUiState.Loading

    val submit = {
        if (isRegisterMode) {
            customerAuthViewModel.register(
                email, password, name,
                restaurantId = if (isStaff) restaurantId else "",
                asDriver = isStaff && isDriver,
                onSuccess = onAuthSuccess
            )
        } else {
            customerAuthViewModel.login(email, password, onAuthSuccess)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isRegisterMode) stringResource(Res.string.auth_screen_title_register) else stringResource(Res.string.auth_screen_login),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(Res.string.auth_screen_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        if (isRegisterMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(Res.string.account_name)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(Res.string.auth_screen_email)) },
            isError = email.isNotEmpty() && !isEmailValid,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth().padding(top = if (isRegisterMode) 12.dp else 0.dp)
        )
        if (email.isNotEmpty() && !isEmailValid) {
            Text(
                stringResource(Res.string.auth_screen_email_invalid),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(Res.string.auth_screen_password),
            isError = password.isNotEmpty() && !isPasswordValid,
            // Enter auf der Tastatur meldet an bzw. registriert, wenn alles stimmt
            onDone = { if (canSubmit) submit() },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        if (password.isNotEmpty() && !isPasswordValid) {
            Text(
                stringResource(Res.string.account_password_min_length),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // nur beim Registrieren: Mitarbeiter geben die ID ihres Restaurants an
        if (isRegisterMode) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(
                    stringResource(Res.string.settings_work_in_restaurant),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(checked = isStaff, onCheckedChange = { isStaff = it })
            }
            if (isStaff) {
                // Fahrer landen später im Fahrer-Bereich, alle anderen im Dashboard
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    FilterChip(
                        selected = !isDriver,
                        onClick = { isDriver = false },
                        label = { Text(stringResource(Res.string.settings_in_team)) }
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        selected = isDriver,
                        onClick = { isDriver = true },
                        label = { Text(stringResource(Res.string.settings_as_driver)) }
                    )
                }
                OutlinedTextField(
                    value = restaurantId,
                    onValueChange = { restaurantId = it },
                    label = { Text(stringResource(Res.string.settings_restaurant_id)) },
                    supportingText = { Text(stringResource(Res.string.auth_screen_restaurant_id_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }

        if (uiState is CustomerAuthUiState.Error) {
            Text(
                text = (uiState as CustomerAuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (uiState is CustomerAuthUiState.ResetEmailSent) {
            Text(
                text = stringResource(Res.string.auth_screen_reset_sent, email),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = submit,
            enabled = canSubmit,
            shape = AppShape.Small,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text(if (isRegisterMode) stringResource(Res.string.auth_screen_register) else stringResource(Res.string.auth_screen_login))
        }

        if (!isRegisterMode) {
            TextButton(
                onClick = { customerAuthViewModel.sendPasswordReset(email) },
                enabled = isEmailValid && uiState !is CustomerAuthUiState.Loading
            ) {
                Text(stringResource(Res.string.auth_screen_forgot_password))
            }
        }

        TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
            Text(if (isRegisterMode) stringResource(Res.string.auth_screen_have_account) else stringResource(Res.string.auth_screen_new_here))
        }
    }

    if (uiState is CustomerAuthUiState.Restored) {
        AlertDialog(
            onDismissRequest = { customerAuthViewModel.continueAfterRestore(onAuthSuccess) },
            title = { Text(stringResource(Res.string.auth_screen_welcome_back)) },
            text = { Text(stringResource(Res.string.auth_screen_restored_text)) },
            confirmButton = {
                TextButton(onClick = { customerAuthViewModel.continueAfterRestore(onAuthSuccess) }) { Text(stringResource(Res.string.common_continue)) }
            }
        )
    }
}
@Preview
@Composable
private fun CustomerAuthScreenPreview() {
    CustomerAuthScreen(
        onAuthSuccess = {},
        customerAuthViewModel = CustomerAuthViewModel(
            AuthRepositoryMock(), RestaurantRepositoryMock(), StaffRepositoryMock(), StorageRepositoryMock(), UserRepositoryMock()
        )
    )
}