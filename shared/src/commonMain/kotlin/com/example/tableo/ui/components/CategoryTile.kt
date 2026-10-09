package com.example.tableo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.util.cuisineImage
import org.jetbrains.compose.resources.painterResource

/**
 * Eine Kategorie-Kachel für die Küchen-Auswahl oben auf der Restaurant-Liste,
 * z.B. "Burger", "Pizza", "Shisha", mit passendem Bild. Ohne Bild steht der Anfangsbuchstabe drin.
 */
@Composable
fun CategoryTile(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val image = cuisineImage(label)

    Column(
        modifier = modifier
            .clip(AppShape.Small)
            .clickable(onClick = onClick)
            .padding(AppSpacing.XS),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(AppShape.Small)
                .background(
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                    else MaterialTheme.colorScheme.surfaceContainer
                )
                .border(
                    width = 2.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = AppShape.Small
                )
        ) {
            if (image != null) {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Text(
                    text = label.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryTilePreview() {
    TableoTheme {
        Row {
            CategoryTile(label = "Burger", selected = true, onClick = {})
            CategoryTile(label = "Sushi", selected = false, onClick = {})
            CategoryTile(label = "Vegan", selected = false, onClick = {})
        }
    }
}
