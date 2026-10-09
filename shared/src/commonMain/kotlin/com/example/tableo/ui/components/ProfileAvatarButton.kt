package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.resources.Res
import com.example.tableo.resources.component_account
import com.example.tableo.resources.component_profile_photo
import org.jetbrains.compose.resources.stringResource

/**
 * Runder Konto-Avatar. Mit [photoUrl] wird das Profilbild gezeigt, sonst zwei Buchstaben aus dem Namen (z.B. "Ali Chihab" -> "AC"),
 * ohne Namen einen Buchstaben aus der E-Mail, ohne beides ein neutrales Personen-Icon.
 * Wird klein oben rechts (Standard 40dp) und groß in der Begrüßung (z.B. 56dp) benutzt.
 */
@Composable
fun ProfileAvatarButton(
    userEmail: String?,
    onClick: () -> Unit,
    displayName: String? = null,
    size: Dp = 40.dp,
    photoUrl: String? = null,
    modifier: Modifier = Modifier
) {
    val initials = displayNameInitials(displayName) ?: userEmail?.take(1)?.uppercase()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = stringResource(Res.string.component_profile_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size)
            )
        } else if (initials.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = stringResource(Res.string.component_account),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

/** "Ali Chihab" -> "AC", "Ali" -> "A". Gibt null zurück, wenn kein Name vorhanden ist. */
private fun displayNameInitials(displayName: String?): String? {
    val words = displayName?.trim()?.split(" ")?.filter { it.isNotBlank() } ?: return null
    if (words.isEmpty()) return null
    val first = words.first().take(1)
    val last = if (words.size > 1) words.last().take(1) else ""
    return (first + last).uppercase().ifBlank { null }
}

@Preview
@Composable
private fun ProfileAvatarButtonPreview() {
    TableoTheme {
        ProfileAvatarButton(userEmail = "ali@example.com", displayName = "Ali Chihab", onClick = {})
    }
}