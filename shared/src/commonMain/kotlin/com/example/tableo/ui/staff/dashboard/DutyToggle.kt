package com.example.tableo.ui.staff.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.resources.Res
import com.example.tableo.resources.staff_on_duty
import com.example.tableo.resources.staff_off_duty
import com.example.tableo.resources.dashboard_duty_hint_on
import com.example.tableo.resources.dashboard_duty_hint_off
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Schalter "Im Dienst": an = Nachricht bei jeder neuen Bestellung, aus = Ruhe. */
@Composable
fun DutyToggle(onDuty: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(
            containerColor = if (onDuty) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (onDuty) stringResource(Res.string.staff_on_duty) else stringResource(Res.string.staff_off_duty),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (onDuty) stringResource(Res.string.dashboard_duty_hint_on) else stringResource(Res.string.dashboard_duty_hint_off),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = onDuty, onCheckedChange = onToggle)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DutyTogglePreview() {
    TableoTheme {
        DutyToggle(onDuty = true, onToggle = {})
    }
}
