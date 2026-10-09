package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home

/** Ein einzelner Reiter: Icon in einer farbigen Pille, wenn ausgewählt, sonst dezent. */
@Composable
fun BottomBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .clip(AppShape.Small)
                .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                .padding(horizontal = AppSpacing.M, vertical = AppSpacing.XS + 2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomBarItemPreview() {
    TableoTheme {
        BottomBarItem(icon = Icons.Filled.Home, label = "Start", selected = true, onClick = {})
    }
}
