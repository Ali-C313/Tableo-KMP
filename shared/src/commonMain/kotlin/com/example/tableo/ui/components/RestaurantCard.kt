package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.tableo.data.model.Restaurant
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing
import com.example.tableo.ui.util.isRestaurantOpenNow

/** Karte für ein Restaurant. Ist es gerade zu, wird sie abgedunkelt. */
@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOpen = isRestaurantOpenNow(restaurant.openingTime, restaurant.closingTime)

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isOpen) 1f else 0.5f),
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Card)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            ) {
                if (restaurant.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = restaurant.imageUrl,
                        contentDescription = restaurant.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ImagePlaceholder()
                }

                Box(
                    modifier = Modifier
                        .padding(AppSpacing.S + AppSpacing.XS)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = restaurant.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (!isOpen) {
                    Box(
                        modifier = Modifier
                            .padding(AppSpacing.S + AppSpacing.XS)
                            .align(Alignment.TopEnd)
                            .background(MaterialTheme.colorScheme.error, AppShape.Large)
                            .padding(horizontal = AppSpacing.S + AppSpacing.XS, vertical = AppSpacing.XS)
                    ) {
                        Text(
                            "Geschlossen",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.M),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        restaurant.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${restaurant.type.ifBlank { "Speisekarte ansehen" }} · ${restaurant.openingTime} – ${restaurant.closingTime} Uhr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (restaurant.address.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            // zwei Zeilen, damit PLZ und Ort nicht abgeschnitten werden
                            Text(
                                text = restaurant.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RestaurantCardPreview() {
    TableoTheme {
        RestaurantCard(
            restaurant = Restaurant(
                id = "layali-lounge",
                name = "Layali Lounge",
                type = "Shisha-Bar",
                address = "Reeperbahn 12, 20359 Hamburg",
                openingTime = "12:00",
                closingTime = "23:00"
            ),
            onClick = {}
        )
    }
}