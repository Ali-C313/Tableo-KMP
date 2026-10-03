package com.example.tableo.ui.guest.orderDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
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
import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.repository.mock.LocationRepositoryMock
import com.example.tableo.ui.components.AddressDialog
import com.example.tableo.ui.components.DropdownPicker
import com.example.tableo.ui.guest.delivery.AddressUiState
import com.example.tableo.ui.guest.delivery.DeliveryViewModel
import com.example.tableo.ui.theme.AppShape
import org.koin.compose.koinInject
import com.example.tableo.ui.util.twoDigits

/** Auswahlmöglichkeiten für die Tischnummer im Picker. */
private val TableNumbers = (1..30).toList()

/**
 * Bestelldetails: Auswahl zwischen Tisch, Abholung und Lieferung. Dazu Tischnummer per Picker,
 * Abholzeit per Uhrzeit-Picker oder die Lieferadresse. Hat der Gast auf der Startseite
 * "Lieferung" gewählt, ist Lieferung schon vorausgewählt.
 *
 * @param restaurantName Name des Restaurants, für die Überschrift
 * @param onConfirmed Wird mit Bestellart, Tischnummer und Abholzeit aufgerufen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    restaurantName: String,
    onConfirmed: (OrderType, Int?, String?) -> Unit,
    onBackClick: () -> Unit = {},
    deliveryViewModel: DeliveryViewModel = koinInject()
) {
    val deliveryMode by deliveryViewModel.deliveryMode.collectAsState()
    val deliveryAddress by deliveryViewModel.address.collectAsState()
    val addressState by deliveryViewModel.addressState.collectAsState()

    var orderType by remember {
        mutableStateOf(if (deliveryMode == DeliveryMode.LIEFERUNG) OrderType.DELIVERY else OrderType.DINE_IN)
    }
    var tableNumber by remember { mutableStateOf<Int?>(null) }
    var pickupTime by remember { mutableStateOf<String?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(initialHour = 18, initialMinute = 0, is24Hour = true)

    LaunchedEffect(addressState) {
        if (addressState is AddressUiState.Success) {
            showAddressDialog = false
            deliveryViewModel.resetAddressState()
        }
    }

    // je nach Bestellart muss Tisch, Abholzeit oder Adresse gewählt sein
    val canContinue = when (orderType) {
        OrderType.DINE_IN -> tableNumber != null
        OrderType.PICKUP -> pickupTime != null
        OrderType.DELIVERY -> deliveryAddress != null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurantName) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text("Wie möchtest du bestellen?", style = MaterialTheme.typography.headlineSmall)

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                OrderTypeChip("Am Tisch", orderType == OrderType.DINE_IN) { orderType = OrderType.DINE_IN }
                OrderTypeChip("Abholen", orderType == OrderType.PICKUP) { orderType = OrderType.PICKUP }
                OrderTypeChip("Lieferung", orderType == OrderType.DELIVERY) { orderType = OrderType.DELIVERY }
            }

            when (orderType) {
                OrderType.DINE_IN -> {
                    Text(
                        text = "An welchem Tisch sitzt du?",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
                    )
                    DropdownPicker(
                        selected = tableNumber,
                        options = TableNumbers,
                        onSelected = { tableNumber = it },
                        placeholder = "Tischnummer wählen",
                        fillWidth = true,
                        optionLabel = { "Tisch $it" }
                    )
                }

                OrderType.PICKUP -> {
                    Text(
                        text = "Wann möchtest du abholen?",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
                    )
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(pickupTime?.let { "$it Uhr" } ?: "Uhrzeit wählen")
                    }
                }

                OrderType.DELIVERY -> {
                    Text(
                        text = "Wohin sollen wir liefern?",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
                    )
                    Card(
                        onClick = { showAddressDialog = true },
                        shape = AppShape.Small,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = deliveryAddress?.text ?: "Adresse eingeben oder Standort teilen",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1f)
                            )
                            Text(
                                "Ändern",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    onConfirmed(
                        orderType,
                        if (orderType == OrderType.DINE_IN) tableNumber else null,
                        if (orderType == OrderType.PICKUP) pickupTime else null
                    )
                },
                enabled = canContinue,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
            ) {
                Text("Weiter")
            }
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        // aus Stunde und Minute einen sauberen "HH:mm"-Text bauen
                        pickupTime = "${timePickerState.hour.twoDigits()}:${timePickerState.minute.twoDigits()}"
                        showTimePicker = false
                    }
                ) { Text("Übernehmen") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Abbrechen") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    if (showAddressDialog) {
        AddressDialog(
            currentAddress = deliveryAddress?.text,
            isLoading = addressState is AddressUiState.Loading,
            errorMessage = (addressState as? AddressUiState.Error)?.message,
            onSearch = { deliveryViewModel.searchAddress(it) },
            onUseLocation = { deliveryViewModel.useCurrentLocation() },
            onPermissionDenied = { deliveryViewModel.onLocationPermissionDenied() },
            onDismiss = {
                showAddressDialog = false
                deliveryViewModel.resetAddressState()
            }
        )
    }
}

/** Ein Chip für die Bestellart, ausgewählt in der Hauptfarbe. */
@Composable
private fun OrderTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
@Preview
@Composable
private fun OrderDetailsScreenPreview() {
    OrderDetailsScreen(
        restaurantName = "Layali Lounge",
        onConfirmed = { _, _, _ -> },
        deliveryViewModel = DeliveryViewModel(LocationRepositoryMock())
    )
}
