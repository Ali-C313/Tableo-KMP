package com.example.tableo.ui.settings

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Dünne Trennlinie zwischen zwei Zeilen in einer SettingsGroup. */
@Composable
fun GroupDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
}

@Preview(showBackground = true)
@Composable
private fun GroupDividerPreview() {
    TableoTheme {
        GroupDivider()
    }
}
