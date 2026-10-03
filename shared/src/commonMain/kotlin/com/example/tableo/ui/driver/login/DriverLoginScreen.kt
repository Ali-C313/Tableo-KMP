package com.example.tableo.ui.driver.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.RestaurantRepositoryMock
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.text.input.ImeAction
import com.example.tableo.ui.components.PasswordTextField

/**
 * Fahrer-Anmeldung per PIN, getrennt vom Personal. Jedes Restaurant hat einen eigenen Fahrer-PIN.
 * Über den Namen werden dem Fahrer seine angenommenen Lieferungen zugeordnet. Ist er angemeldet,
 * kommt der Name aus seinem Konto, sonst gibt er ihn selbst ein.
 *
 * @param onLoginSuccess Wird mit dem gefundenen Restaurant und dem Fahrer-Namen aufgerufen
 */
@Composable
fun DriverLoginScreen(
    onLoginSuccess: (Restaurant, String) -> Unit,
    driverLoginViewModel: DriverLoginViewModel = koinViewModel()
) {
    val uiState by driverLoginViewModel.uiState.collectAsState()
    var pin by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val accountName = driverLoginViewModel.accountName
    val driverName = accountName ?: name

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Fahrer-Anmeldung", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Gib den Fahrer-PIN deines Restaurants ein",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        if (accountName != null) {
            Text(
                text = "Angemeldet als $accountName",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        } else {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Dein Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
        }

        PasswordTextField(
            value = pin, onValueChange = { pin = it },
            label = "Fahrer-PIN",
            isPin = true,
            onDone = {
                if (pin.isNotBlank() && driverName.isNotBlank()) driverLoginViewModel.login(pin, driverName, onLoginSuccess)
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState is DriverLoginUiState.Error) {
            Text(
                text = (uiState as DriverLoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = { driverLoginViewModel.login(pin, driverName, onLoginSuccess) },
            enabled = pin.isNotBlank() && driverName.isNotBlank() && uiState !is DriverLoginUiState.Loading,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) { Text("Anmelden") }
    }
}
@Preview
@Composable
private fun DriverLoginScreenPreview() {
    DriverLoginScreen(
        onLoginSuccess = { _, _ -> },
        driverLoginViewModel = DriverLoginViewModel(RestaurantRepositoryMock(), AuthRepositoryMock())
    )
}
