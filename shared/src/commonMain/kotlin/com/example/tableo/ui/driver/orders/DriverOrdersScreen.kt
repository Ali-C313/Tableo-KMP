package com.example.tableo.ui.driver.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.mock.LocationRepositoryMock
import com.example.tableo.data.repository.mock.NotificationRepositoryMock
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.resources.Res
import com.example.tableo.resources.common_back
import com.example.tableo.resources.driver_accept
import com.example.tableo.resources.driver_available
import com.example.tableo.resources.driver_available_empty
import com.example.tableo.resources.driver_delivered
import com.example.tableo.resources.driver_move_down
import com.example.tableo.resources.driver_move_up
import com.example.tableo.resources.driver_my_tour
import com.example.tableo.resources.driver_navigation
import com.example.tableo.resources.driver_resume_tracking
import com.example.tableo.resources.driver_start_tour
import com.example.tableo.resources.driver_tour_empty
import com.example.tableo.resources.driver_tracking_active
import com.example.tableo.resources.error_with_message
import com.example.tableo.ui.components.DriverOrderCard
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.util.RequestNotificationPermission
import com.example.tableo.util.rememberLocationPermissionRequest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import io.ktor.http.encodeURLParameter

/**
 * Fahrer-Bereich mit zwei Listen: oben die eigene Tour in Lieferreihenfolge (mit Pfeilen
 * umsortierbar), darunter alle abholbereiten Lieferungen zum Annehmen.
 * Der Standort wird gesendet, solange die App offen ist.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverOrdersScreen(
    onBackClick: () -> Unit = {},
    driverOrdersViewModel: DriverOrdersViewModel = koinViewModel()
) {
    val uiState by driverOrdersViewModel.uiState.collectAsState()
    val isTracking by driverOrdersViewModel.isTracking.collectAsState()
    val trackingError by driverOrdersViewModel.trackingError.collectAsState()
    val uriHandler = LocalUriHandler.current

    RequestNotificationPermission()

    // Lieferung merken, die angenommen werden soll, bis die Berechtigung beantwortet ist
    var pendingAccept by remember { mutableStateOf<Order?>(null) }
    val requestLocation = rememberLocationPermissionRequest { granted ->
        val toAccept = pendingAccept
        pendingAccept = null
        if (!granted) driverOrdersViewModel.onLocationPermissionDenied()
        when {
            toAccept != null -> driverOrdersViewModel.accept(toAccept, sendLocation = granted)
            granted -> driverOrdersViewModel.startTour()
        }
    }

    // öffnet Google Maps mit Route zur Lieferadresse
    val openNavigation: (Order) -> Unit = { order ->
        val destination = order.deliveryLocation
            ?.let { "${it.latitude},${it.longitude}" }
            ?: order.deliveryAddress.orEmpty()
        // öffnet sich auf Android in Google Maps, auf dem iPhone im Browser oder in der Google-Maps-App
        uriHandler.openUri("https://www.google.com/maps/dir/?api=1&destination=${destination.encodeURLParameter()}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(driverOrdersViewModel.driverName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            driverOrdersViewModel.restaurantName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.common_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is DriverOrdersUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is DriverOrdersUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.error_with_message, state.message), color = MaterialTheme.colorScheme.error)
                }
            }

            is DriverOrdersUiState.Success -> {
                val tourRunning = state.tour.any { it.status == OrderStatus.UNTERWEGS }
                val hasWaiting = state.tour.any { it.status == OrderStatus.ANGENOMMEN }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SectionHeader(stringResource(Res.string.driver_my_tour, state.tour.size))
                        trackingError?.let { message ->
                            Text(
                                message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        if (isTracking) {
                            Text(
                                stringResource(Res.string.driver_tracking_active),
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        // Losfahren, oder nach Neustart der App das Senden wieder aufnehmen
                        if (hasWaiting || (tourRunning && !isTracking)) {
                            Button(
                                onClick = requestLocation,
                                shape = AppShape.Small,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Text(if (hasWaiting) stringResource(Res.string.driver_start_tour) else stringResource(Res.string.driver_resume_tracking))
                            }
                        }
                        if (state.tour.isEmpty()) {
                            Text(
                                stringResource(Res.string.driver_tour_empty),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    itemsIndexed(state.tour) { index, order ->
                        DriverOrderCard(order = order, position = index + 1) {
                            if (index == 0 && order.status == OrderStatus.UNTERWEGS) {
                                Button(
                                    onClick = { driverOrdersViewModel.finishDelivery(order) },
                                    shape = AppShape.Small,
                                    modifier = Modifier.weight(1f)
                                ) { Text(stringResource(Res.string.driver_delivered)) }
                            }
                            OutlinedButton(
                                onClick = { openNavigation(order) },
                                shape = AppShape.Small,
                                modifier = Modifier.weight(1f)
                            ) { Text(stringResource(Res.string.driver_navigation)) }
                            IconButton(
                                onClick = { driverOrdersViewModel.move(order, -1) },
                                enabled = index > 0
                            ) { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = stringResource(Res.string.driver_move_up)) }
                            IconButton(
                                onClick = { driverOrdersViewModel.move(order, 1) },
                                enabled = index < state.tour.lastIndex
                            ) { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = stringResource(Res.string.driver_move_down)) }
                        }
                    }

                    item {
                        SectionHeader(
                            stringResource(Res.string.driver_available, state.available.size),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        if (state.available.isEmpty()) {
                            Text(
                                stringResource(Res.string.driver_available_empty),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    items(state.available) { order: Order ->
                        DriverOrderCard(order = order) {
                            Button(
                                onClick = {
                                    pendingAccept = order
                                    requestLocation()
                                },
                                shape = AppShape.Small,
                                modifier = Modifier.weight(1f)
                            ) { Text(stringResource(Res.string.driver_accept)) }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DriverOrdersScreenPreview() {
    val savedStateHandle = SavedStateHandle(
        mapOf("restaurantId" to "1", "restaurantName" to "Layali Lounge", "driverName" to "Omar")
    )
    DriverOrdersScreen(
        driverOrdersViewModel = DriverOrdersViewModel(
            savedStateHandle, OrderRepositoryMock(), LocationRepositoryMock(), NotificationRepositoryMock()
        )
    )
}
