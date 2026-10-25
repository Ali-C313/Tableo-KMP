package com.example.tableo.ui.settings

import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.tableo.data.repository.mock.RestaurantRepositoryMock
import com.example.tableo.data.repository.mock.StaffRepositoryMock
import com.example.tableo.data.repository.mock.StorageRepositoryMock
import com.example.tableo.data.repository.mock.ThemeRepositoryMock
import com.example.tableo.data.repository.mock.UserRepositoryMock
import com.example.tableo.data.repository.mock.LanguageRepositoryMock
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.model.StaffAccess
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.text.style.TextAlign
import com.example.tableo.data.enums.AppLanguage
import com.example.tableo.data.enums.LegalPage
import com.example.tableo.util.FormatHelper
import com.example.tableo.util.LanguageState
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.tableo.resources.Res
import com.example.tableo.resources.language_system
import com.example.tableo.resources.legal_imprint_title
import com.example.tableo.resources.legal_privacy_title
import com.example.tableo.resources.legal_terms_title
import com.example.tableo.resources.settings_app_version
import com.example.tableo.resources.settings_appearance
import com.example.tableo.resources.settings_delete_account
import com.example.tableo.resources.settings_delete_account_hint
import com.example.tableo.resources.settings_driver_area
import com.example.tableo.resources.settings_driver_area_hint
import com.example.tableo.resources.settings_greeting
import com.example.tableo.resources.settings_greeting_name
import com.example.tableo.resources.settings_language
import com.example.tableo.resources.settings_login_or_register
import com.example.tableo.resources.settings_logout
import com.example.tableo.resources.settings_my_orders
import com.example.tableo.resources.settings_my_restaurant
import com.example.tableo.resources.settings_my_restaurant_hint
import com.example.tableo.resources.settings_not_logged_in
import com.example.tableo.resources.settings_not_logged_in_hint
import com.example.tableo.resources.settings_personal_data
import com.example.tableo.resources.settings_personal_data_hint
import com.example.tableo.resources.settings_section_account
import com.example.tableo.resources.settings_section_legal
import com.example.tableo.resources.settings_section_settings
import com.example.tableo.resources.settings_section_staff
import com.example.tableo.resources.settings_theme_dark
import com.example.tableo.resources.settings_theme_light
import com.example.tableo.resources.settings_theme_system
import com.example.tableo.resources.settings_work_in_restaurant
import com.example.tableo.resources.settings_work_in_restaurant_hint
import org.jetbrains.compose.resources.stringResource

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
 * @param onLegalClick Öffnet Impressum, Datenschutz oder AGB
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
    onLegalClick: (LegalPage) -> Unit = {},
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
    val firstName = displayName?.let { FormatHelper.firstName(it) }

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
                    text = if (isLoggedIn && firstName != null) stringResource(Res.string.settings_greeting_name, firstName) else stringResource(Res.string.settings_greeting),
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
                        Text(stringResource(Res.string.settings_not_logged_in), style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = stringResource(Res.string.settings_not_logged_in_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Button(
                            onClick = onLoginClick,
                            shape = AppShape.Small,
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                        ) { Text(stringResource(Res.string.settings_login_or_register)) }
                    }
                }
            }

            if (isLoggedIn) {
                SectionTitle(stringResource(Res.string.settings_section_account))
                SettingsGroup {
                    SettingsRow(
                        icon = Icons.Filled.Person,
                        title = stringResource(Res.string.settings_personal_data),
                        subtitle = stringResource(Res.string.settings_personal_data_hint),
                        onClick = onAccountInfoClick
                    )
                    GroupDivider()
                    SettingsRow(
                        icon = Icons.AutoMirrored.Filled.List,
                        title = stringResource(Res.string.settings_my_orders),
                        onClick = onOrdersTabClick
                    )
                }
            }

            // Mitarbeiter und Fahrer erkennt die App am Konto, ein PIN ist nicht nötig
            if (isLoggedIn) {
                SectionTitle(stringResource(Res.string.settings_section_staff))
                SettingsGroup {
                    val access = staffAccess
                    if (access == null) {
                        // für Konten, die schon vor der Mitarbeiter-Registrierung angelegt wurden
                        SettingsRow(
                            icon = Icons.Filled.Storefront,
                            title = stringResource(Res.string.settings_work_in_restaurant),
                            subtitle = stringResource(Res.string.settings_work_in_restaurant_hint),
                            onClick = { showLinkDialog = true }
                        )
                    } else {
                        if (access.canOpenDashboard) {
                            SettingsRow(
                                icon = Icons.Filled.Storefront,
                                title = stringResource(Res.string.settings_my_restaurant),
                                subtitle = stringResource(Res.string.settings_my_restaurant_hint, access.restaurant.name),
                                onClick = { onStaffDashboardClick(access.restaurant) }
                            )
                        }
                        if (access.canOpenDashboard && access.canOpenDriverArea) GroupDivider()
                        // Lieferanten und die Leitung
                        if (access.canOpenDriverArea) {
                            SettingsRow(
                                icon = Icons.Filled.PedalBike,
                                title = stringResource(Res.string.settings_driver_area),
                                subtitle = stringResource(Res.string.settings_driver_area_hint, access.restaurant.name),
                                onClick = { onDriverAreaClick(access) }
                            )
                        }
                    }
                }
            }

            SectionTitle(stringResource(Res.string.settings_section_settings))
            SettingsGroup {
                PickerRow(icon = Icons.Filled.DarkMode, title = stringResource(Res.string.settings_appearance)) {
                    DropdownPicker(
                        selected = ThemeState.mode,
                        options = ThemeMode.entries,
                        onSelected = { settingsViewModel.setThemeMode(it) },
                        optionLabel = { mode ->
                            when (mode) {
                                ThemeMode.SYSTEM -> stringResource(Res.string.settings_theme_system)
                                ThemeMode.LIGHT -> stringResource(Res.string.settings_theme_light)
                                ThemeMode.DARK -> stringResource(Res.string.settings_theme_dark)
                            }
                        }
                    )
                }
                GroupDivider()
                PickerRow(icon = Icons.Filled.Language, title = stringResource(Res.string.settings_language)) {
                    DropdownPicker(
                        selected = LanguageState.language,
                        options = AppLanguage.entries,
                        onSelected = { settingsViewModel.setLanguage(it) },
                        // "System" heißt in jeder Sprache anders, die anderen stehen immer in ihrer eigenen Sprache
                        optionLabel = { language ->
                            if (language == AppLanguage.SYSTEM) stringResource(Res.string.language_system) else language.nativeName
                        }
                    )
                }
            }

            SectionTitle(stringResource(Res.string.settings_section_legal))
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Filled.Info,
                    title = stringResource(Res.string.legal_imprint_title),
                    onClick = { onLegalClick(LegalPage.IMPRESSUM) }
                )
                GroupDivider()
                SettingsRow(
                    icon = Icons.Filled.PrivacyTip,
                    title = stringResource(Res.string.legal_privacy_title),
                    onClick = { onLegalClick(LegalPage.DATENSCHUTZ) }
                )
                GroupDivider()
                SettingsRow(
                    icon = Icons.Filled.Description,
                    title = stringResource(Res.string.legal_terms_title),
                    onClick = { onLegalClick(LegalPage.AGB) }
                )
            }

            if (isLoggedIn) {
                SettingsGroup(modifier = Modifier.padding(top = 24.dp)) {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = stringResource(Res.string.settings_logout),
                        showChevron = false,
                        tint = MaterialTheme.colorScheme.error,
                        onClick = { settingsViewModel.logout(onDone = onLoggedOut) }
                    )
                    GroupDivider()
                    SettingsRow(
                        icon = Icons.Filled.Delete,
                        title = stringResource(Res.string.settings_delete_account),
                        subtitle = stringResource(Res.string.settings_delete_account_hint),
                        showChevron = false,
                        tint = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteDialog = true }
                    )
                }
            }

            Text(
                text = stringResource(Res.string.settings_app_version, "1.0"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                textAlign = TextAlign.Center
            )
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

@Preview
@Composable
private fun SettingsScreenPreview() {
    TableoTheme {
        SettingsScreen(
            settingsViewModel = SettingsViewModel(AuthRepositoryMock(), StaffRepositoryMock(), RestaurantRepositoryMock(), StorageRepositoryMock(), ThemeRepositoryMock(), UserRepositoryMock(), LanguageRepositoryMock())
        )
    }
}
@Preview
@Composable
private fun SettingsScreenLoggedOutPreview() {
    TableoTheme {
        SettingsScreen(
            settingsViewModel = SettingsViewModel(AuthRepositoryMock(loggedIn = false), StaffRepositoryMock(), RestaurantRepositoryMock(), StorageRepositoryMock(), ThemeRepositoryMock(), UserRepositoryMock(), LanguageRepositoryMock())
        )
    }
}
