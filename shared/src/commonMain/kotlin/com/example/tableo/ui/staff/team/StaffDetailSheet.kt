package com.example.tableo.ui.staff.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.ui.components.DropdownPicker
import com.example.tableo.ui.components.ProfileAvatarButton
import com.example.tableo.data.enums.label
import com.example.tableo.resources.Res
import com.example.tableo.resources.team_status
import com.example.tableo.resources.staff_on_duty
import com.example.tableo.resources.staff_off_duty
import com.example.tableo.resources.team_joined_since
import com.example.tableo.resources.team_role
import com.example.tableo.resources.team_choose_role
import com.example.tableo.resources.team_remove_from_team
import com.example.tableo.resources.team_remove_title
import com.example.tableo.resources.team_remove_text
import com.example.tableo.resources.team_remove_confirm
import com.example.tableo.resources.common_cancel
import com.example.tableo.util.FormatHelper
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Details zu einem Mitarbeiter mit Rollen-Auswahl und Entfernen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDetailSheet(
    profile: StaffProfile,
    isMe: Boolean,
    canManage: Boolean,
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
            InfoLine(
                stringResource(Res.string.team_status),
                if (profile.onDuty) stringResource(Res.string.staff_on_duty) else stringResource(Res.string.staff_off_duty)
            )
            if (profile.joinedAt > 0) {
                InfoLine(stringResource(Res.string.team_joined_since), FormatHelper.dateTime(profile.joinedAt))
            }

            Text(
                stringResource(Res.string.team_role),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
            )
            // nur die Leitung vergibt Rollen, die eigene Rolle bleibt, damit man sich nicht aussperrt
            if (canManage && !isMe) {
                DropdownPicker(
                    selected = profile.role,
                    options = StaffRole.entries,
                    onSelected = onRoleSelected,
                    placeholder = stringResource(Res.string.team_choose_role),
                    fillWidth = true,
                    optionLabel = { it.label },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(profile.role.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth())
            }

            if (error != null) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
            }

            // sich selbst kann man nicht rauswerfen
            if (canManage && !isMe) {
                OutlinedButton(
                    onClick = { showRemoveDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().padding(top = 28.dp)
                ) {
                    Text(stringResource(Res.string.team_remove_from_team))
                }
            }
        }
    }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text(stringResource(Res.string.team_remove_title, profile.name)) },
            text = { Text(stringResource(Res.string.team_remove_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showRemoveDialog = false
                    onRemove()
                }) {
                    Text(stringResource(Res.string.team_remove_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) { Text(stringResource(Res.string.common_cancel)) }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffDetailSheetPreview() {
    TableoTheme {
        StaffDetailSheet(
            profile = StaffProfile(id = "u2", restaurantId = "1", userId = "u2", name = "Mona", email = "mona@example.com", role = StaffRole.KELLNER, onDuty = true),
            isMe = false,
            canManage = true,
            error = null,
            onRoleSelected = {},
            onRemove = {},
            onDismiss = {}
        )
    }
}
