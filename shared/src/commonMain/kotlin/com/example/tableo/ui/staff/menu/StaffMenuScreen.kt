package com.example.tableo.ui.staff.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.model.MenuItem
import com.example.tableo.data.repository.mock.MenuRepositoryMock
import com.example.tableo.ui.components.StaffMenuItemRow
import org.koin.compose.viewmodel.koinViewModel
import com.example.tableo.resources.Res
import com.example.tableo.resources.staff_menu_title
import com.example.tableo.resources.staff_menu_add
import com.example.tableo.resources.error_with_message
import com.example.tableo.resources.staff_menu_empty
import org.jetbrains.compose.resources.stringResource

/**
 * Speisekarten-Verwaltung fürs Personal: Artikel ansehen, bearbeiten, verfügbar/ausverkauft schalten.
 *
 * @param onAddItemClick Wird aufgerufen, um einen neuen Artikel anzulegen
 * @param onEditItemClick Wird mit der ID des angetippten Artikels aufgerufen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffMenuScreen(
    onAddItemClick: () -> Unit,
    onEditItemClick: (String) -> Unit,
    onBackClick: () -> Unit = {},
    staffMenuViewModel: StaffMenuViewModel = koinViewModel()
) {
    val uiState by staffMenuViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.staff_menu_title, staffMenuViewModel.restaurantName),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = { TextButton(onClick = onAddItemClick) { Text(stringResource(Res.string.staff_menu_add)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is StaffMenuUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is StaffMenuUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.error_with_message, state.message), color = MaterialTheme.colorScheme.error)
                }
            }

            is StaffMenuUiState.Success -> {
                if (state.items.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Text(stringResource(Res.string.staff_menu_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.items) { item: MenuItem ->
                            StaffMenuItemRow(
                                item = item,
                                onEditClick = { onEditItemClick(item.id) },
                                onAvailabilityChange = { staffMenuViewModel.setAvailability(item.id, it) },
                                onDeleteConfirmed = { staffMenuViewModel.deleteItem(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Preview
@Composable
private fun StaffMenuScreenPreview() {
    val savedStateHandle =
        SavedStateHandle(mapOf("restaurantId" to "1", "restaurantName" to "Layali Lounge"))
    StaffMenuScreen(
        onAddItemClick = {},
        onEditItemClick = {},
        staffMenuViewModel = StaffMenuViewModel(savedStateHandle, MenuRepositoryMock())
    )
}