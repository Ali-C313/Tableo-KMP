package com.example.tableo.ui.staff.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import com.example.tableo.ui.theme.OnDutyGreen
import androidx.compose.ui.unit.dp
import com.example.tableo.resources.Res
import com.example.tableo.resources.staff_on_duty
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Grüner Punkt mit "Im Dienst". */
@Composable
fun DutyBadge() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(OnDutyGreen))
        Spacer(Modifier.width(4.dp))
        Text(stringResource(Res.string.staff_on_duty), style = MaterialTheme.typography.labelMedium, color = OnDutyGreen)
    }
}

@Preview(showBackground = true)
@Composable
private fun DutyBadgePreview() {
    TableoTheme {
        DutyBadge()
    }
}
