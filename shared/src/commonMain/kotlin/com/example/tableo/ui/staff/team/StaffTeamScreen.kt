package com.example.tableo.ui.staff.team

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.StaffRepositoryMock
import com.example.tableo.ui.components.DropdownPicker
import com.example.tableo.ui.components.ProfileAvatarButton
import com.example.tableo.ui.util.asDateTime
import org.koin.compose.viewmodel.koinViewModel

/** Team eines Restaurants mit Profilbild, Rolle und wer gerade im Dienst ist. Antippen zeigt Details, Rolle und Entfernen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffTeamScreen(
    onBackClick: () -> Unit = {},
    staffTeamViewModel: StaffTeamViewModel = koinViewModel()
) {
    val uiState by staffTeamViewModel.uiState.collectAsState()
    val actionError by staffTeamViewModel.actionError.collectAsState()

    // nur die ID merken, damit das Blatt neue Rollen direkt aus der Liste zeigt
    var selectedId by remember { mutableStateOf<String?>(null) }
    val selected = (uiState as? StaffTeamUiState.Success)?.staff?.firstOrNull { it.id == selectedId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Team · ${staffTeamViewModel.restaurantName}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
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
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            RestaurantIdCard(staffTeamViewModel.restaurantId)

            when (val state = uiState) {
                is StaffTeamUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is StaffTeamUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Fehler: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }

                is StaffTeamUiState.Success -> {
                    if (state.staff.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Noch kein Team registriert.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.staff, key = { it.id }) { profile ->
                                StaffRow(
                                    profile = profile,
                                    isMe = profile.userId == staffTeamViewModel.currentUserId,
                                    onClick = { selectedId = profile.id }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selected != null) {
        StaffDetailSheet(
            profile = selected,
            isMe = selected.userId == staffTeamViewModel.currentUserId,
            error = actionError,
            onRoleSelected = { role -> staffTeamViewModel.updateRole(selected, role) },
            onRemove = { staffTeamViewModel.removeStaff(selected) { selectedId = null } },
            onDismiss = {
                selectedId = null
                staffTeamViewModel.clearActionError()
            }
        )
    }
}

/** Die ID gibt der Chef neuen Mitarbeitern, damit sie sich beim Registrieren eintragen können. */
@Composable
private fun RestaurantIdCard(restaurantId: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Restaurant-ID für neue Mitarbeiter", style = MaterialTheme.typography.labelMedium)
            SelectionContainer {
                Text(
                    restaurantId,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                "Beim Registrieren \"Ich arbeite in einem Restaurant\" einschalten und diese ID eintragen.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Eine Zeile in der Team-Liste: Bild, Name, Rolle. */
@Composable
private fun StaffRow(profile: StaffProfile, isMe: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            ProfileAvatarButton(
                userEmail = profile.email,
                displayName = profile.name,
                photoUrl = profile.photoUrl,
                onClick = onClick,
                size = 48.dp
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isMe) "${profile.name} (Du)" else profile.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.role.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (profile.role == StaffRole.OHNE) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                    if (profile.onDuty) {
                        Spacer(Modifier.width(10.dp))
                        DutyBadge()
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Details zu einem Mitarbeiter mit Rollen-Auswahl und Entfernen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StaffDetailSheet(
    profile: StaffProfile,
    isMe: Boolean,
    error: String?,
    onRoleSelected: (StaffRole) -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit
) {
    var showRemoveDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp)
        ) {
            ProfileAvatarButton(
                userEmail = profile.email,
                displayName = profile.name,
                photoUrl = profile.photoUrl,
                onClick = {},
                size = 96.dp
            )
            Spacer(Modifier.height(12.dp))
            Text(profile.name, style = MaterialTheme.typography.headlineSmall)
            if (profile.email.isNotBlank()) {
                SelectionContainer {
                    Text(
                        profile.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            InfoLine("Status", if (profile.onDuty) "Im Dienst" else "Nicht im Dienst")
            if (profile.joinedAt > 0) {
                InfoLine("Dabei seit", profile.joinedAt.asDateTime())
            }

            Text(
                "Rolle",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
            )
            DropdownPicker(
                selected = profile.role,
                options = StaffRole.entries,
                onSelected = onRoleSelected,
                placeholder = "Rolle wählen",
                fillWidth = true,
                optionLabel = { it.label },
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
            }

            // sich selbst kann man nicht rauswerfen
            if (!isMe) {
                OutlinedButton(
                    onClick = { showRemoveDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().padding(top = 28.dp)
                ) {
                    Text("Aus dem Team entfernen")
                }
            }
        }
    }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("${profile.name} entfernen?") },
            text = { Text("Das Konto bleibt bestehen, hat aber keinen Zugriff mehr auf das Dashboard.") },
            confirmButton = {
                TextButton(onClick = {
                    showRemoveDialog = false
                    onRemove()
                }) {
                    Text("Entfernen", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) { Text("Abbrechen") }
            }
        )
    }
}

/** Grüner Punkt mit "Im Dienst". */
@Composable
private fun DutyBadge() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(OnDutyGreen))
        Spacer(Modifier.width(4.dp))
        Text("Im Dienst", style = MaterialTheme.typography.labelMedium, color = OnDutyGreen)
    }
}

private val OnDutyGreen = Color(0xFF2E7D32)

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}

@Preview
@Composable
private fun StaffTeamScreenPreview() {
    val savedStateHandle =
        SavedStateHandle(mapOf("restaurantId" to "1", "restaurantName" to "Layali Lounge"))
    StaffTeamScreen(
        staffTeamViewModel = StaffTeamViewModel(savedStateHandle, StaffRepositoryMock(), AuthRepositoryMock())
    )
}
