package com.example.tableo.ui.components

import com.example.tableo.resources.Res
import com.example.tableo.resources.component_guest_no_image
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tableo.ui.theme.TableoTheme

/**
 * Platzhalter für fehlende Bilder, statt einer reinen Farbfläche.
 *
 * @param fontSize Schriftgröße, damit der Text auch in sehr kleinen Kacheln (z.B. Menü-Vorschau) noch passt
 */
@Composable
fun ImagePlaceholder(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 11.sp
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.component_guest_no_image),
            style = MaterialTheme.typography.bodySmall,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagePlaceholderPreview() {
    TableoTheme {
        ImagePlaceholder(modifier = Modifier.size(96.dp))
    }
}