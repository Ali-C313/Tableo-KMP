package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.BottomTab
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing

/**
 * Schwebende, abgerundete Navigationsleiste statt der flachen Material3-Standardleiste.
 *
 * @param selectedTab Aktuell aktiver Reiter (bekommt die farbige Hervorhebung)
 */
@Composable
fun AppBottomBar(
    selectedTab: BottomTab,
    onHomeClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = AppSpacing.L, vertical = AppSpacing.M),
        shape = AppShape.Large,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = AppElevation.Floating
    ) {
        Row(
            modifier = Modifier.padding(vertical = AppSpacing.S + AppSpacing.XS),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarItem(
                Icons.Filled.Home,
                "Start",
                selectedTab == BottomTab.HOME,
                onHomeClick
            )
            BottomBarItem(
                Icons.AutoMirrored.Filled.List,
                "Bestellungen",
                selectedTab == BottomTab.ORDERS,
                onOrdersClick
            )
            BottomBarItem(
                Icons.Filled.AccountCircle,
                "Konto",
                selectedTab == BottomTab.SETTINGS,
                onSettingsClick
            )
        }
    }
}

/** Ein einzelner Reiter: Icon in einer farbigen Pille, wenn ausgewählt, sonst dezent. */
@Composable
private fun BottomBarItem(
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

@Preview
@Composable
private fun AppBottomBarPreview() {
    AppBottomBar(
        selectedTab = BottomTab.HOME,
        onHomeClick = { },
        onOrdersClick = { },
        onSettingsClick = { }
    )
}