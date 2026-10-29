package com.example.tableo.ui.staff.team

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.StaffRole
import com.example.tableo.data.model.StaffProfile
import com.example.tableo.ui.components.ProfileAvatarButton
import com.example.tableo.data.enums.label
import com.example.tableo.resources.Res
import com.example.tableo.resources.team_member_me
import com.example.tableo.resources.team_details
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Eine Zeile in der Team-Liste: Bild, Name, Rolle. */
@Composable
fun StaffRow(profile: StaffProfile, isMe: Boolean, onClick: () -> Unit) {
    Card(
        shape = AppShape.Small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Flat),
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
                    text = if (isMe) stringResource(Res.string.team_member_me, profile.name) else profile.name,
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
                contentDescription = stringResource(Res.string.team_details),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffRowPreview() {
    TableoTheme {
        StaffRow(profile = StaffProfile(id = "u2", restaurantId = "1", userId = "u2", name = "Mona", email = "mona@example.com", role = StaffRole.KELLNER, onDuty = true), isMe = false, onClick = {})
    }
}
