package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.tableo.data.model.MenuItem
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing
import com.example.tableo.ui.util.asEuro

/**
 * Eine Zeile in der Speisekarte. Die Menge wird hier nicht eingestellt, nur hinzufügen;
 * die Menge wird später im Warenkorb angepasst. Nicht verfügbare Artikel werden abgedunkelt
 * und lassen sich nicht in den Warenkorb legen.
 *
 * @param imageUrl Optionale Foto-URL; ohne Bild erscheint "Kein Bild vorhanden"
 * @param cartQuantity Aktuelle Menge dieses Artikels im Warenkorb, nur zur Anzeige
 * @param convertedPriceText Optionaler Preis in einer Fremdwährung, z.B. "$14.07"
 * @param onAddClick Wird aufgerufen, wenn der Warenkorb-Button angetippt wird
 */
@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    imageUrl: String = "",
    cartQuantity: Int = 0,
    convertedPriceText: String? = null,
    onAddClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().alpha(if (menuItem.available) 1f else 0.5f),
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.S + AppSpacing.XS),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S + AppSpacing.XS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(AppShape.Small)
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = menuItem.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ImagePlaceholder()
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    menuItem.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = menuItem.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (!menuItem.available) {
                    Text(
                        text = "Nicht verfügbar",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = AppSpacing.XS + 2.dp)
                    ) {
                        Text(
                            text = menuItem.price.asEuro(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (convertedPriceText != null) {
                            Text(
                                text = " · ≈ $convertedPriceText",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = AppSpacing.XS + 2.dp)
                            )
                        } else if (cartQuantity > 0) {
                            Text(
                                text = " · $cartQuantity im Warenkorb",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(start = AppSpacing.XS + 2.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (menuItem.available) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .then(
                        if (menuItem.available) Modifier.clickable(onClick = onAddClick) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Zum Warenkorb hinzufügen",
                    tint = if (menuItem.available) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuItemCardPreview() {
    TableoTheme {
        MenuItemCard(
            menuItem = MenuItem(
                id = "1",
                restaurantId = "layali-lounge",
                name = "Wassermelone",
                description = "Süß, leicht",
                price = 13.0,
                category = "Shisha"
            ),
            cartQuantity = 2
        )
    }
}