package com.example.tableo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tableo.ui.theme.Espresso10
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PedalBike

/** Ein Teil des Schalters: gewählt = weiße Kapsel mit Text, sonst nur das Icon. */
@Composable
fun DeliveryModeSegment(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        animationSpec = tween(250)
    )
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(30.dp)
            .shadow(if (isSelected) 2.dp else 0.dp, CircleShape)
            .clip(CircleShape)
            .background(background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = if (isSelected) 12.dp else 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(16.dp))
            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(tween(250)) + fadeIn(tween(250)),
                exit = shrinkHorizontally(tween(250)) + fadeOut(tween(150))
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    // die Kapsel ist auch im Dunkelmodus weiß, deshalb immer dunkle Schrift
                    color = Espresso10,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeliveryModeSegmentPreview() {
    TableoTheme {
        DeliveryModeSegment(label = "Lieferung", icon = Icons.Outlined.PedalBike, isSelected = true, onClick = {})
    }
}
