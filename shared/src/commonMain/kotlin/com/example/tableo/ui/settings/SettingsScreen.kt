package com.example.tableo.ui.settings

import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.tableo.data.repository.mock.RestaurantRepositoryMock
import com.example.tableo.data.repository.mock.StaffRepositoryMock
import com.example.tableo.data.repository.mock.StorageRepositoryMock
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.model.StaffAccess
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilterChip
import com.example.tableo.ui.components.PasswordTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.BottomTab
import com.example.tableo.data.enums.ThemeMode
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.ui.components.AppBottomBar
import com.example.tableo.ui.components.DropdownPicker
import com.example.tableo.ui.components.ProfileAvatarButton
import com.example.tableo.ui.components.SettingsRow
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.ThemeState
import org.koin.compose.viewmodel.koinViewModel

/**
 * Konto-Seite, angelehnt an Lieferando: oben die Begrüßung mit Profilbild, darunter
 * "Dein Konto", "Einstellungen" und bei Mitarbeitern der Zugang zum Dashboard oder Fahrer-Bereich. Das Profilbild und
 * "Persönliche Daten" führen zu den Kontoinformationen.
 *
 * @param onAccountInfoClick Öffnet Name, E-Mail und Passwort zum Bearbeiten
 * @param onLoginClick Führt zum Kunden-Login bzw. zur Registrierung
 * @param onStaffDashboardClick Öffnet das Dashboard des eigenen Restaurants (nur bei Mitarbeitern sichtbar)
 * @param onDriverAreaClick Öffnet den Fahrer-Bereich (nur bei Lieferanten sichtbar)
 * @param onLoggedOut Wird nach dem Abmelden und nach dem Löschen des Kontos aufgerufen
 */
@Composable
fun SettingsScreen(
    onHomeTabClick: () -> Unit = {},
    onOrdersTabClick: () -> Unit = {},
    onAccountInfoClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onStaffDashboardClick: (Restaurant) -> Unit = {},
    onDriverAreaClick: (StaffAccess) -> Unit = {},
    onLoggedOut: () -> Unit = {},
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val isLoggedIn = settingsViewModel.isLoggedIn
    val staffAccess by settingsViewModel.staffAccess.collectAsState()
    val deleteError by settingsViewModel.deleteError.collectAsState()
    val isDeleting by settingsViewModel.isDeleting.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val linkError by settingsViewModel.linkError.collectAsState()
    val photoUrl by settingsViewModel.photoUrl.collectAsState()
    var showLinkDialog by remember { mutableStateOf(false) }
    val displayName = settingsViewModel.currentName?.takeIf { it.isNotBlank() }
        ?: settingsViewModel.currentEmail?.substringBefore("@")
    // für die Begrüßung reicht der Vorname
    val firstName = displayName?.substringBefore(" ")

    Scaffold(
        bottomBar = {
            AppBottomBar(
                selectedTab = BottomTab.SETTINGS,
                onHomeClick = onHomeTabClick,
                onOrdersClick = onOrdersTabClick,
                onSettingsClick = {}
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
            // Begrüßung links, Profilbild rechts
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 36.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLoggedIn && firstName != null) "Hallo, $firstName!" else "Hallo!",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                ProfileAvatarButton(
                    userEmail = settingsViewModel.currentEmail,
                    displayName = settingsViewModel.currentName,
                    onClick = if (isLoggedIn) onAccountInfoClick else onLoginClick,
                    size = 64.dp,
                    photoUrl = photoUrl
                )
            }

            if (!isLoggedIn) {
                Card(
                    shape = AppShape.Medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Noch nicht angemeldet", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Mit Konto siehst du deine Bestellungen und ihren Status",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Button(
                            onClick = onLoginClick,
                            shape = AppShape.Small,
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                        ) { Text("Anmelden oder registrieren") }
                    }
                }
            }

            if (isLoggedIn) {
                SectionTitle("Dein Konto")
                SettingsRow(
                    icon = Icons.Filled.Person,
                    title = "Persönliche Daten",
                    subtitle = "Name, E-Mail und Passwort",
                    onClick = onAccountInfoClick
                )
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.List,
                    title = "Meine Bestellungen",
                    onClick = onOrdersTabClick
                )
            }

            SectionTitle("Einstellungen")
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Erscheinungsbild", style = MaterialTheme.typography.bodyLarge)
                DropdownPicker(
                    selected = ThemeState.mode,
                    options = ThemeMode.entries,
                    onSelected = { ThemeState.mode = it },
                    optionLabel = { mode ->
                        when (mode) {
                            ThemeMode.SYSTEM -> "Standard"
                            ThemeMode.LIGHT -> "Hell"
                            ThemeMode.DARK -> "Dunkel"
                        }
                    }
                )
            }

            if (isLoggedIn) {
                SectionTitle("Personal")
            }
            // Mitarbeiter und Fahrer erkennt die App am Konto, ein PIN ist nicht nötig
            staffAccess?.let { access ->
                if (access.canOpenDashboard) {
                    SettingsRow(
                        icon = Icons.Filled.Person,
                        title = "Mein Restaurant",
                        subtitle = "${access.restaurant.name} · Bestellungen, Menü, Team",
                        onClick = { onStaffDashboardClick(access.restaurant) }
                    )
                }
                // Lieferanten und die Leitung
                if (access.canOpenDriverArea) {
                    SettingsRow(
                        icon = Icons.Filled.Place,
                        title = "Fahrer-Bereich",
                        subtitle = "${access.restaurant.name} · Lieferungen annehmen und Standort teilen",
                        onClick = { onDriverAreaClick(access) }
                    )
                }
            }
            // für Konten, die schon vor der Mitarbeiter-Registrierung angelegt wurden
            if (isLoggedIn && staffAccess == null) {
                SettingsRow(
                    icon = Icons.Filled.Person,
                    title = "Ich arbeite in einem Restaurant",
                    subtitle = "Konto mit der Restaurant-ID verknüpfen, im Team oder als Fahrer",
                    onClick = { showLinkDialog = true }
                )
            }

            if (isLoggedIn) {
                HorizontalDivider(modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Abmelden",
                    showChevron = false,
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { settingsViewModel.logout(onDone = onLoggedOut) }
                )
                SettingsRow(
                    icon = Icons.Filled.Delete,
                    title = "Konto löschen",
                    subtitle = "Konto und Team-Zugang endgültig entfernen",
                    showChevron = false,
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { showDeleteDialog = true }
                )
            }
        }
    }

    if (showLinkDialog) {
        LinkRestaurantDialog(
            errorMessage = linkError,
            onConfirm = { restaurantId, asDriver ->
                settingsViewModel.linkRestaurant(restaurantId, asDriver) { showLinkDialog = false }
            },
            onDismiss = {
                showLinkDialog = false
                settingsViewModel.clearLinkError()
            }
        )
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            errorMessage = deleteError,
            isDeleting = isDeleting,
            onConfirm = { password ->
                settingsViewModel.deleteAccount(password) {
                    showDeleteDialog = false
                    onLoggedOut()
                }
            },
            onDismiss = {
                showDeleteDialog = false
                settingsViewModel.clearDeleteError()
            }
        )
    }
}

/** Überschrift für einen Bereich der Konto-Seite. */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 32.dp, bottom = 4.dp)
    )
}
@Preview
@Composable
private fun SettingsScreenPreview() {
    TableoTheme {
        SettingsScreen(
            settingsViewModel = SettingsViewModel(AuthRepositoryMock(), StaffRepositoryMock(), RestaurantRepositoryMock(), StorageRepositoryMock())
        )
    }
}
@Preview
@Composable
private fun SettingsScreenLoggedOutPreview() {
    TableoTheme {
        SettingsScreen(
            settingsViewModel = SettingsViewModel(AuthRepositoryMock(loggedIn = false), StaffRepositoryMock(), RestaurantRepositoryMock(), StorageRepositoryMock())
        )
    }
}

/** Dialog zum Eintragen der Restaurant-ID, die der Chef im Team-Reiter sieht. */
@Composable
private fun LinkRestaurantDialog(
    errorMessage: String?,
    onConfirm: (restaurantId: String, asDriver: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var restaurantId by remember { mutableStateOf("") }
    var isDriver by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restaurant verknüpfen") },
        text = {
            Column {
                Text(
                    "Die Restaurant-ID bekommst du von deinem Chef, sie steht im Dashboard unter Team.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = restaurantId,
                    onValueChange = { restaurantId = it },
                    label = { Text("Restaurant-ID") },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    FilterChip(selected = !isDriver, onClick = { isDriver = false }, label = { Text("Im Team") })
                    Spacer(Modifier.width(8.dp))
                    FilterChip(selected = isDriver, onClick = { isDriver = true }, label = { Text("Als Fahrer") })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(restaurantId, isDriver) }, enabled = restaurantId.isNotBlank()) {
                Text("Verknüpfen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}

/** Sicherheitsabfrage mit Passwort, bevor das Konto endgültig gelöscht wird. */
@Composable
private fun DeleteAccountDialog(
    errorMessage: String?,
    isDeleting: Boolean,
    onConfirm: (password: String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = { Text("Konto löschen?") },
        text = {
            Column {
                Text(
                    "Dein Konto wird endgültig gelöscht und du wirst aus deinem Restaurant-Team entfernt. " +
                        "Das lässt sich nicht rückgängig machen.",
                    style = MaterialTheme.typography.bodyMedium
                )
                PasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Passwort zur Bestätigung",
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
                Text(if (isDeleting) "Wird gelöscht…" else "Endgültig löschen", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) { Text("Abbrechen") }
        }
    )
}
