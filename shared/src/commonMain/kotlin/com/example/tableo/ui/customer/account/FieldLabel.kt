package com.example.tableo.ui.customer.account

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Kleine Überschrift über einem Eingabefeld. */
@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun FieldLabelPreview() {
    TableoTheme {
        FieldLabel(text = "E-Mail-Adresse")
    }
}
