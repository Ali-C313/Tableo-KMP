package com.example.tableo.ui.legal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.LegalPage
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.resources.Res
import com.example.tableo.resources.common_back
import com.example.tableo.resources.legal_last_updated
import org.jetbrains.compose.resources.stringResource

/** Impressum, Datenschutz oder AGB als einfache Textseite. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    page: LegalPage,
    onBackClick: () -> Unit = {}
) {
    val content = legalContent(page)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(content.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                content.sections.forEach { (heading, body) ->
                    Text(
                        text = heading,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 20.dp, bottom = 6.dp)
                    )
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(Res.string.legal_last_updated),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun LegalScreenPreview() {
    TableoTheme {
        LegalScreen(page = LegalPage.IMPRESSUM)
    }
}
