package com.example.tableo.ui.customer.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.RestaurantRepositoryMock
import com.example.tableo.data.repository.mock.StaffRepositoryMock
import com.example.tableo.data.repository.mock.StorageRepositoryMock
import com.example.tableo.data.repository.mock.ThemeRepositoryMock
import com.example.tableo.ui.settings.SettingsUiState
import com.example.tableo.ui.settings.SettingsViewModel
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppShape
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.text.input.ImeAction
import com.example.tableo.ui.components.PasswordTextField
import com.example.tableo.ui.components.ProfileAvatarButton
import com.example.tableo.ui.util.rememberImagePicker

/**
 * Kontoinformationen: Profilbild, Name, E-Mail und Passwort ändern, alles mit einem Button speichern.
 * Das aktuelle Passwort wird nur abgefragt, wenn sich E-Mail oder Passwort ändern.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onBackClick: () -> Unit = {},
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val photoUrl by settingsViewModel.photoUrl.collectAsState()
    val isUploadingPhoto by settingsViewModel.isUploadingPhoto.collectAsState()
    val pickPhoto = rememberImagePicker { bytes -> settingsViewModel.updateProfilePhoto(bytes) }

    var name by remember { mutableStateOf(settingsViewModel.currentName ?: "") }
    var email by remember { mutableStateOf(settingsViewModel.currentEmail ?: "") }
    var newPassword by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }

    val nameChanged = name.isNotBlank() && name != (settingsViewModel.currentName ?: "")
    val emailChanged = email != (settingsViewModel.currentEmail ?: "")
    val needsCurrentPassword = emailChanged || newPassword.isNotBlank()
    val emailValid = email.contains("@") && email.contains(".")
    val passwordValid = newPassword.isBlank() || newPassword.length >= 6

    val canSave = (nameChanged || emailChanged || newPassword.isNotBlank()) &&
            emailValid && passwordValid &&
            (!needsCurrentPassword || currentPassword.isNotBlank()) &&
            uiState !is SettingsUiState.Saving

    // nach dem Speichern die Passwortfelder leeren
    LaunchedEffect(uiState) {
        if (uiState is SettingsUiState.Saved) {
            newPassword = ""
            currentPassword = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kontoinformationen") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Profilbild, das auch der Chef in der Team-Liste sieht
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    ProfileAvatarButton(
                        userEmail = settingsViewModel.currentEmail,
                        displayName = settingsViewModel.currentName,
                        onClick = pickPhoto,
                        size = 96.dp,
                        photoUrl = photoUrl
                    )
                    if (isUploadingPhoto) {
                        CircularProgressIndicator(modifier = Modifier.size(96.dp))
                    }
                }
                TextButton(onClick = pickPhoto, enabled = !isUploadingPhoto) {
                    Text(if (photoUrl.isBlank()) "Profilbild hinzufügen" else "Profilbild ändern")
                }
            }

            FieldLabel("Name")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = AppShape.Small,
                modifier = Modifier.fillMaxWidth()
            )

            FieldLabel("E-Mail-Adresse")
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                isError = email.isNotEmpty() && !emailValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                shape = AppShape.Small,
                modifier = Modifier.fillMaxWidth()
            )

            FieldLabel("Neues Passwort")
            PasswordTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = "Leer lassen, wenn es bleiben soll",
                isError = !passwordValid,
                shape = AppShape.Small,
                modifier = Modifier.fillMaxWidth()
            )
            if (!passwordValid) {
                Text(
                    "Mindestens 6 Zeichen.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (needsCurrentPassword) {
                FieldLabel("Aktuelles Passwort")
                PasswordTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    shape = AppShape.Small,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Zur Sicherheit nötig, wenn du E-Mail oder Passwort änderst.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Button(
                onClick = { settingsViewModel.saveAccountInfo(name, email, currentPassword, newPassword) },
                enabled = canSave,
                shape = AppShape.Large,
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
            ) {
                Text(if (uiState is SettingsUiState.Saving) "Wird gespeichert…" else "Änderungen speichern")
            }

            if (uiState is SettingsUiState.Error) {
                Text(
                    text = (uiState as SettingsUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            if (uiState is SettingsUiState.Saved) {
                Text(
                    text = if (emailChanged) {
                        "Gespeichert. Bitte bestätige die neue E-Mail über den Link in deinem Postfach."
                    } else {
                        "Gespeichert."
                    },
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

/** Kleine Überschrift über einem Eingabefeld. */
@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
    )
}
@Preview
@Composable
private fun AccountScreenPreview() {
    TableoTheme {
        AccountScreen(settingsViewModel = SettingsViewModel(AuthRepositoryMock(), StaffRepositoryMock(), RestaurantRepositoryMock(), StorageRepositoryMock(), ThemeRepositoryMock()))
    }
}