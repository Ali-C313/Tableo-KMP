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
 * "Dein Konto", "Einstellungen" und bei Mitarbeitern der Zugang zum Restaurant-Dashboard. Das Profilbild und
 * "Persönliche Daten" führen zu den Kontoinformationen.
 *
 * @param onAccountInfoClick Öffnet Name, E-Mail und Passwort zum Bearbeiten
 * @param onLoginClick Führt zum Kunden-Login bzw. zur Registrierung
 * @param onStaffDashboardClick Öffnet das Dashboard des eigenen Restaurants (nur bei Mitarbeitern sichtbar)
 * @param onDriverAreaClick Führt zur Fahrer-Anmeldung
 * @param onLoggedOut Wird nach dem Abmelden aufgerufen
 */
@Composable
fun SettingsScreen(
    onHomeTabClick: () -> Unit = {},
    onOrdersTabClick: () -> Unit = {},
    onAccountInfoClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onStaffDashboardClick: (Restaurant) -> Unit = {},
    onDriverAreaClick: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val isLoggedIn = settingsViewModel.isLoggedIn
    val staffRestaurant by settingsViewModel.staffRestaurant.collectAsState()
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

            SectionTitle("Personal")
            // Mitarbeiter erkennt die App am Konto, ein extra PIN-Login ist nicht nötig
            staffRestaurant?.let { restaurant ->
                SettingsRow(
                    icon = Icons.Filled.Person,
                    title = "Mein Restaurant",
                    subtitle = "${restaurant.name} · Bestellungen, Menü, Team",
                    onClick = { onStaffDashboardClick(restaurant) }
                )
            }
            // für Konten, die schon vor der Mitarbeiter-Registrierung angelegt wurden
            if (isLoggedIn && staffRestaurant == null) {
                SettingsRow(
                    icon = Icons.Filled.Person,
                    title = "Ich arbeite in einem Restaurant",
                    subtitle = "Konto mit der Restaurant-ID verknüpfen",
                    onClick = { showLinkDialog = true }
                )
            }
            SettingsRow(
                icon = Icons.Filled.Place,
                title = "Fahrer-Bereich",
                subtitle = "Lieferungen annehmen und Standort teilen",
                onClick = onDriverAreaClick
            )

            if (isLoggedIn) {
                HorizontalDivider(modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Abmelden",
                    showChevron = false,
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { settingsViewModel.logout(onDone = onLoggedOut) }
                )
            }
        }
    }

    if (showLinkDialog) {
        LinkRestaurantDialog(
            errorMessage = linkError,
            onConfirm = { restaurantId -> settingsViewModel.linkRestaurant(restaurantId) { showLinkDialog = false } },
            onDismiss = {
                showLinkDialog = false
                settingsViewModel.clearLinkError()
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
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var restaurantId by remember { mutableStateOf("") }

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
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(restaurantId) }, enabled = restaurantId.isNotBlank()) {
                Text("Verknüpfen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}
