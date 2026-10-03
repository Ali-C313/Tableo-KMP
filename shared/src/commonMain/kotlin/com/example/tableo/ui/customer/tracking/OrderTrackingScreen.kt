package com.example.tableo.ui.customer.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.model.Coordinates
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.ui.components.StatusPill
import com.example.tableo.ui.components.TrackingMap
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.util.distanceInKm
import com.example.tableo.ui.util.estimatedMinutesViaStops
import com.example.tableo.ui.util.firstName
import com.example.tableo.ui.util.formatDistance
import org.koin.compose.viewmodel.koinViewModel

/**
 * Live-Tracking einer Lieferung. Sobald ein Fahrer die Bestellung hat, zeigt die Karte
 * Fahrer und Lieferadresse, darunter die geschätzte Ankunft. Hat der Fahrer vorher noch
 * andere Lieferungen, werden die Umwege in die Zeit eingerechnet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    onBackClick: () -> Unit = {},
    orderTrackingViewModel: OrderTrackingViewModel = koinViewModel()
) {
    val uiState by orderTrackingViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lieferung verfolgen") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is OrderTrackingUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is OrderTrackingUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text("Fehler: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }

            is OrderTrackingUiState.Success -> {
                val order = state.order
                val driverOnTheWay = order.status == OrderStatus.ANGENOMMEN || order.status == OrderStatus.UNTERWEGS

                Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    if (driverOnTheWay && order.driverLocation != null) {
                        TrackingMap(
                            driver = order.driverLocation,
                            destination = order.deliveryLocation,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (driverOnTheWay) "Warte auf den Standort des Fahrers…"
                                else "Sobald ein Fahrer deine Bestellung hat, siehst du ihn hier live auf der Karte.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    TrackingInfoCard(order = order, stopsBefore = state.stopsBefore)
                }
            }
        }
    }
}

/** Status, geschätzte Ankunft und wie viele Lieferungen der Fahrer vorher noch hat. */
@Composable
private fun TrackingInfoCard(order: Order, stopsBefore: List<Coordinates>) {
    val driverLocation = order.driverLocation
    val destination = order.deliveryLocation
    // aus Datenschutzgründen nur der Vorname
    val driverName = order.driverName?.firstName() ?: "Der Fahrer"

    val minutes = if (driverLocation != null && destination != null) {
        estimatedMinutesViaStops(driverLocation, stopsBefore, destination)
    } else null
    val directKm = if (driverLocation != null && destination != null) distanceInKm(driverLocation, destination) else null

    val headline = when (order.status) {
        OrderStatus.OFFEN, OrderStatus.IN_ZUBEREITUNG -> "Dein Essen wird zubereitet"
        OrderStatus.FERTIG, OrderStatus.ABHOLBEREIT -> "Wartet auf einen Fahrer"
        OrderStatus.ANGENOMMEN, OrderStatus.UNTERWEGS ->
            minutes?.let { "In ca. $it Min. bei dir" } ?: "$driverName hat deine Bestellung"
        OrderStatus.ERLEDIGT, OrderStatus.BEZAHLT -> "Zugestellt. Guten Appetit!"
        OrderStatus.STORNIERT -> "Bestellung storniert"
    }

    val subline = when (order.status) {
        OrderStatus.ANGENOMMEN -> "$driverName holt deine Bestellung gerade ab"
        OrderStatus.UNTERWEGS -> when {
            stopsBefore.isNotEmpty() ->
                "$driverName ist unterwegs und liefert vorher noch ${stopsBefore.size} Bestellung(en)"
            directKm != null -> "$driverName ist auf dem Weg zu dir, noch ${formatDistance(directKm)}"
            else -> "$driverName ist auf dem Weg zu dir"
        }
        else -> null
    }

    Card(
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            StatusPill(order.status, doneLabel = "Zugestellt")
            Text(
                headline,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 10.dp)
            )
            subline?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                "${order.restaurantName} · ${order.deliveryAddress.orEmpty()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
@Preview
@Composable
private fun OrderTrackingScreenPreview() {
    OrderTrackingScreen(
        orderTrackingViewModel = OrderTrackingViewModel(
            SavedStateHandle(mapOf("orderId" to "3")),
            OrderRepositoryMock()
        )
    )
}
