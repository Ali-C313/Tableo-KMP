package com.example.tableo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PedalBike
import androidx.compose.material.icons.outlined.TakeoutDining
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.data.enums.label
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Kapsel mit Lieferung links und Abholung rechts. */
@Composable
fun DeliveryModeSwitch(
    selected: DeliveryMode,
    onSelected: (DeliveryMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), CircleShape)
            .padding(3.dp)
    ) {
        DeliveryModeSegment(
            label = DeliveryMode.LIEFERUNG.label,
            icon = Icons.Outlined.PedalBike,
            isSelected = selected == DeliveryMode.LIEFERUNG,
            onClick = { onSelected(DeliveryMode.LIEFERUNG) }
        )
        DeliveryModeSegment(
            label = DeliveryMode.ABHOLUNG.label,
            icon = Icons.Outlined.TakeoutDining,
            isSelected = selected == DeliveryMode.ABHOLUNG,
            onClick = { onSelected(DeliveryMode.ABHOLUNG) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeliveryModeSwitchPreview() {
    TableoTheme {
        DeliveryModeSwitch(selected = DeliveryMode.LIEFERUNG, onSelected = {})
    }
}
