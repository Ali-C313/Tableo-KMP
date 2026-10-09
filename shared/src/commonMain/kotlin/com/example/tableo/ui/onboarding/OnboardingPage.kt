package com.example.tableo.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tableo.ui.theme.AppShape
import androidx.compose.ui.tooling.preview.Preview
import com.example.tableo.ui.theme.TableoTheme

/** Eine Seite im Onboarding: Bild, Titel, Text und ein oder zwei Buttons. */
@Composable
fun OnboardingPage(
    image: @Composable () -> Unit,
    title: String,
    description: String,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        image()
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
        Button(
            onClick = onPrimaryClick,
            shape = AppShape.Small,
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) { Text(primaryButtonText) }
        if (secondaryButtonText != null) {
            OutlinedButton(
                onClick = onSecondaryClick,
                shape = AppShape.Small,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) { Text(secondaryButtonText) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPagePreview() {
    TableoTheme {
        OnboardingPage(
            image = { Text("🍽") },
            title = "Willkommen bei Tableo",
            description = "Bestellen am Tisch, zum Abholen oder per Lieferung.",
            primaryButtonText = "Weiter",
            onPrimaryClick = {}
        )
    }
}
