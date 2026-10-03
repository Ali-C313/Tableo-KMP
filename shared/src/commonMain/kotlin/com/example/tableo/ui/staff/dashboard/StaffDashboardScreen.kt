package com.example.tableo.ui.staff.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.NotificationRepositoryMock
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.data.repository.mock.StaffRepositoryMock
import com.example.tableo.ui.components.OrderCard
import com.example.tableo.ui.staff.shift.StaffShiftViewModel
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.util.RequestNotificationPermission
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Bestell-Dashboard für das Personal eines Restaurants. Restaurant-ID und -Name
 * kommen über die Route direkt ins ViewModel, nicht mehr als Parameter hierher.
 *
 * @param onBackClick Wird aufgerufen, wenn der Zurück-Pfeil angetippt wird
 * @param onArchiveClick Wird aufgerufen, wenn "Archiv" angetippt wird
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    onBackClick: () -> Unit = {},
    onArchiveClick: () -> Unit = {},
    onAnalyticsClick: () -> Unit = {},
    onManageMenuClick: () -> Unit = {},
    onTeamClick: () -> Unit = {},
    staffDashboardViewModel: StaffDashboardViewModel = koinViewModel(),
    staffShiftViewModel: StaffShiftViewModel = koinInject()
) {
    val uiState by staffDashboardViewModel.uiState.collectAsState()
    val profile by staffShiftViewModel.profile.collectAsState()
    val onDuty = profile?.onDuty == true

    if (onDuty) RequestNotificationPermission()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = staffDashboardViewModel.restaurantName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Zurück"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Mehr")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Menü") },
                                onClick = { menuExpanded = false; onManageMenuClick() }
                            )
                            DropdownMenuItem(
                                text = { Text("Team") },
                                onClick = { menuExpanded = false; onTeamClick() }
                            )
                            DropdownMenuItem(
                                text = { Text("Statistik") },
                                onClick = { menuExpanded = false; onAnalyticsClick() }
                            )
                            DropdownMenuItem(
                                text = { Text("Archiv") },
                                onClick = { menuExpanded = false; onArchiveClick() }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                // nur bei Mitarbeitern mit eigenem Profil
                if (profile != null) {
                    DutyToggle(onDuty = onDuty, onToggle = staffShiftViewModel::setOnDuty)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is StaffDashboardUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is StaffDashboardUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text ="Fehler: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is StaffDashboardUiState.Success -> {
                if (state.orders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aktuell keine offenen Bestellungen.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.orders) { order: Order ->
                            OrderCard(
                                order = order,
                                onAdvanceStatus = { staffDashboardViewModel.advanceStatus(order) },
                                onMarkAsDone = { staffDashboardViewModel.markAsDone(order) },
                                onCancel = { staffDashboardViewModel.cancelOrder(order) }
                            )
                        }
                    }
                }
            }
        }
    }
}
/** Schalter "Im Dienst": an = Nachricht bei jeder neuen Bestellung, aus = Ruhe. */
@Composable
private fun DutyToggle(onDuty: Boolean, onToggle: (Boolean) -> Unit) {
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
                    text = if (onDuty) "Im Dienst" else "Nicht im Dienst",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (onDuty) "Du bekommst eine Nachricht bei neuen Bestellungen" else "Keine Nachrichten zu neuen Bestellungen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = onDuty, onCheckedChange = onToggle)
        }
    }
}

@Preview
@Composable
private fun StaffDashboardScreenPreview() {
    val savedStateHandle =
        SavedStateHandle(mapOf("restaurantId" to "1", "restaurantName" to "Layali Lounge"))
    StaffDashboardScreen(
        staffDashboardViewModel = StaffDashboardViewModel(savedStateHandle, OrderRepositoryMock()),
        staffShiftViewModel = StaffShiftViewModel(
            OrderRepositoryMock(),
            StaffRepositoryMock(),
            AuthRepositoryMock(),
            NotificationRepositoryMock()
        ).apply { start() }
    )
}