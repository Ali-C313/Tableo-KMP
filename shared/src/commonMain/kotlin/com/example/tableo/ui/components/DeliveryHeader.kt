package com.example.tableo.ui.components

import com.example.tableo.resources.Res
import com.example.tableo.resources.address_change
import com.example.tableo.resources.address_enter
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.ui.theme.TableoTheme

/**
 * Kopfzeile der Startseite: links die Adresse mit Pfeil, rechts der Schalter Lieferung/Abholung.
 * Die gewählte Art steht als weiße Kapsel mit Icon und Text, die andere nur als Icon daneben.
 *
 * @param address Gewählte Adresse, null wenn noch keine eingegeben wurde
 * @param onAddressClick Öffnet den Adress-Dialog
 */
@Composable
fun DeliveryHeader(
    address: String?,
    deliveryMode: DeliveryMode,
    onAddressClick: () -> Unit,
    onModeSelected: (DeliveryMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clip(CircleShape)
                .clickable(onClick = onAddressClick)
                .padding(vertical = 4.dp)
        ) {
            Text(
                // nur Straße und Hausnummer, wie bei Lieferando
                text = address?.substringBefore(",") ?: stringResource(Res.string.address_enter),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(Res.string.address_change),
                modifier = Modifier.padding(start = 2.dp).size(20.dp)
            )
        }

        DeliveryModeSwitch(
            selected = deliveryMode,
            onSelected = onModeSelected,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeliveryHeaderPreview() {
    TableoTheme {
        DeliveryHeader(
            address = "Binnenweg 32, 20357 Hamburg",
            deliveryMode = DeliveryMode.LIEFERUNG,
            onAddressClick = {},
            onModeSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeliveryHeaderPickupPreview() {
    TableoTheme {
        DeliveryHeader(
            address = null,
            deliveryMode = DeliveryMode.ABHOLUNG,
            onAddressClick = {},
            onModeSelected = {}
        )
    }
}
