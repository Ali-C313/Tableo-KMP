package com.example.tableo.ui.staff.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.resources.Res
import com.example.tableo.resources.team_restaurant_id_label
import com.example.tableo.resources.team_restaurant_id_hint
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Die ID gibt der Chef neuen Mitarbeitern, damit sie sich beim Registrieren eintragen können. */
@Composable
fun RestaurantIdCard(restaurantId: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(Res.string.team_restaurant_id_label), style = MaterialTheme.typography.labelMedium)
            SelectionContainer {
                Text(
                    restaurantId,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                stringResource(Res.string.team_restaurant_id_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RestaurantIdCardPreview() {
    TableoTheme {
        RestaurantIdCard(restaurantId = "layali-lounge")
    }
}
