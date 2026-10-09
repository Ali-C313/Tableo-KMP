package com.example.tableo.ui.driver.orders

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Überschrift über einer Liste im Fahrer-Bereich, z.B. "Meine Tour". */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(title, style = MaterialTheme.typography.titleLarge, modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun SectionHeaderPreview() {
    TableoTheme {
        SectionHeader(title = "Meine Tour (2)")
    }
}
