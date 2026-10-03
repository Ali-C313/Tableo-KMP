package com.example.tableo.ui.customer.orderDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.ui.components.CancelOrderDialog
import com.example.tableo.ui.components.StatusPill
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.util.asDateTime
import com.example.tableo.ui.util.asEuro
import com.example.tableo.ui.util.estimatedReadyTimeText
import com.example.tableo.ui.util.firstName
import com.example.tableo.ui.util.orderNumber
import org.koin.compose.viewmodel.koinViewModel

/**
 * Alle Details einer Bestellung für den Kunden: Bestellnummer und ID, Restaurant, Zeit,
 * Bestellart, Bezahlart, Positionen mit Preisen und Summe. Von hier aus kann er die
 * Lieferung verfolgen, erneut bestellen oder stornieren, solange noch nicht zubereitet wird.
 *
 * @param onTrackOrder Wird mit der Bestell-ID aufgerufen, um die Lieferung zu verfolgen
 * @param onReorder Wird mit der Bestellung aufgerufen, um sie erneut zu bestellen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrderDetailScreen(
    onBackClick: () -> Unit = {},
    onTrackOrder: (String) -> Unit = {},
    onReorder: (Order) -> Unit = {},
    myOrderDetailViewModel: MyOrderDetailViewModel = koinViewModel()
) {
    val uiState by myOrderDetailViewModel.uiState.collectAsState()
    val cancelError by myOrderDetailViewModel.cancelError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(cancelError) {
        cancelError?.let {
            snackbarHostState.showSnackbar(it)
            myOrderDetailViewModel.clearCancelError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bestelldetails") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is MyOrderDetailUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is MyOrderDetailUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text("Fehler: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }

            is MyOrderDetailUiState.Success -> {
                val order = state.order
                val canTrack = order.orderType == OrderType.DELIVERY && !order.status.isDone
                val canCancel = order.status == OrderStatus.OFFEN

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    HeaderCard(order)
                    DetailsCard(order)
                    ItemsCard(order)

                    if (canTrack) {
                        Button(
                            onClick = { onTrackOrder(order.id) },
                            shape = AppShape.Small,
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                        ) { Text("Live verfolgen") }
                    }
                    OutlinedButton(
                        onClick = { onReorder(order) },
                        shape = AppShape.Small,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) { Text("Erneut bestellen") }
                    if (canCancel) {
                        TextButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Bestellung stornieren", color = MaterialTheme.colorScheme.error) }
                    }
                }

                if (showCancelDialog) {
                    CancelOrderDialog(
                        text = "Das geht nur, solange ${order.restaurantName} noch nicht mit der Zubereitung angefangen hat.",
                        onConfirm = { myOrderDetailViewModel.cancelOrder() },
                        onDismiss = { showCancelDialog = false }
                    )
                }
            }
        }
    }
}

/** Bestellnummer, Status, Restaurant und Zeitpunkt. Die volle ID lässt sich markieren und kopieren. */
@Composable
private fun HeaderCard(order: Order) {
    DetailCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bestellung ${order.orderNumber()}", style = MaterialTheme.typography.titleLarge)
            StatusPill(order.status, doneLabel = "Abgeschlossen")
        }
        Text(
            order.restaurantName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            "Bestellt am ${order.createdAt.asDateTime()} Uhr",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // lange gedrückt halten zum Kopieren, z.B. für Rückfragen beim Restaurant
        SelectionContainer {
            Text(
                "Bestell-ID: ${order.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

/** Bestellart, Adresse bzw. Tisch, Bezahlart, Fahrer und Fertigzeit. */
@Composable
private fun DetailsCard(order: Order) {
    val typeText = when (order.orderType) {
        OrderType.DINE_IN -> "Am Tisch"
        OrderType.PICKUP -> "Abholung"
        OrderType.DELIVERY -> "Lieferung"
    }
    val placeText = when (order.orderType) {
        OrderType.DINE_IN -> "Tisch ${order.tableNumber}"
        OrderType.PICKUP -> "Abholung um ${order.pickupTime} Uhr"
        OrderType.DELIVERY -> order.deliveryAddress.orEmpty()
    }

    DetailCard(modifier = Modifier.padding(top = 12.dp)) {
        DetailRow("Bestellart", typeText)
        DetailRow(if (order.orderType == OrderType.DELIVERY) "Lieferadresse" else "Wo / Wann", placeText)
        DetailRow("Bezahlart", order.paymentMethod.label)
        DetailRow("Bestellt als", order.customerName.ifBlank { "Gast" })
        order.driverName?.let { DetailRow("Fahrer", it.firstName()) }
        if (!order.status.isDone) {
            DetailRow("Voraussichtlich", order.estimatedReadyTimeText())
        }
        if (order.status == OrderStatus.STORNIERT) {
            DetailRow("Storniert von", if (order.cancelledByRestaurant) "Restaurant" else "dir")
        }
    }
}

/** Alle Positionen mit Menge, Einzelpreis und Zwischensumme, darunter die Gesamtsumme. */
@Composable
private fun ItemsCard(order: Order) {
    DetailCard(modifier = Modifier.padding(top = 12.dp)) {
        Text("Deine Bestellung", style = MaterialTheme.typography.titleMedium)
        order.items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${item.quantity}× ${item.name}", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "je ${item.pricePerItem.asEuro()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(item.subtotal.asEuro(), style = MaterialTheme.typography.bodyLarge)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gesamt", style = MaterialTheme.typography.titleMedium)
            Text(
                order.totalPrice.asEuro(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Eine Zeile "Bezeichnung ... Wert". */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

/** Gleiche Karte für alle Abschnitte. */
@Composable
private fun DetailCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        shape = AppShape.Medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}
@Preview
@Composable
private fun MyOrderDetailScreenPreview() {
    MyOrderDetailScreen(
        myOrderDetailViewModel = MyOrderDetailViewModel(
            SavedStateHandle(mapOf("orderId" to "3")),
            OrderRepositoryMock()
        )
    )
}
