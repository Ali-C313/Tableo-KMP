package com.example.tableo.ui.guest.orderDetails

import com.example.tableo.resources.Res
import com.example.tableo.resources.common_back
import com.example.tableo.resources.common_cancel
import com.example.tableo.resources.common_continue
import com.example.tableo.resources.delivery_mode_delivery
import com.example.tableo.resources.guest_apply
import com.example.tableo.resources.order_details_address_placeholder
import com.example.tableo.resources.order_details_change
import com.example.tableo.resources.order_details_delivery_question
import com.example.tableo.resources.order_details_pickup_placeholder
import com.example.tableo.resources.order_details_pickup_question
import com.example.tableo.resources.order_details_table_placeholder
import com.example.tableo.resources.order_details_table_question
import com.example.tableo.resources.order_details_title
import com.example.tableo.resources.order_details_type_pickup
import com.example.tableo.resources.order_details_type_table
import com.example.tableo.resources.order_table
import com.example.tableo.resources.time_oclock
import com.example.tableo.util.FormatHelper
import org.jetbrains.compose.resources.stringResource
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
import com.example.tableo.data.repository.mock.DeliveryPreferencesRepositoryMock
import com.example.tableo.data.repository.mock.LocationRepositoryMock
import com.example.tableo.ui.components.AddressDialog
import com.example.tableo.ui.components.DropdownPicker
import com.example.tableo.ui.guest.delivery.AddressUiState
import com.example.tableo.ui.guest.delivery.DeliveryViewModel
import com.example.tableo.ui.theme.AppShape
import org.koin.compose.koinInject

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.common_back))
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
            Text(stringResource(Res.string.order_details_title), style = MaterialTheme.typography.headlineSmall)

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                OrderTypeChip(stringResource(Res.string.order_details_type_table), orderType == OrderType.DINE_IN) { orderType = OrderType.DINE_IN }
                OrderTypeChip(stringResource(Res.string.order_details_type_pickup), orderType == OrderType.PICKUP) { orderType = OrderType.PICKUP }
                OrderTypeChip(stringResource(Res.string.delivery_mode_delivery), orderType == OrderType.DELIVERY) { orderType = OrderType.DELIVERY }
            }

            when (orderType) {
                OrderType.DINE_IN -> {
                    Text(
                        text = stringResource(Res.string.order_details_table_question),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
                    )
                    DropdownPicker(
                        selected = tableNumber,
                        options = TableNumbers,
                        onSelected = { tableNumber = it },
                        placeholder = stringResource(Res.string.order_details_table_placeholder),
                        fillWidth = true,
                        optionLabel = { stringResource(Res.string.order_table, it.toString()) }
                    )
                }

                OrderType.PICKUP -> {
                    Text(
                        text = stringResource(Res.string.order_details_pickup_question),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
                    )
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(pickupTime?.let { stringResource(Res.string.time_oclock, it) } ?: stringResource(Res.string.order_details_pickup_placeholder))
                    }
                }

                OrderType.DELIVERY -> {
                    Text(
                        text = stringResource(Res.string.order_details_delivery_question),
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
                                text = deliveryAddress?.text ?: stringResource(Res.string.order_details_address_placeholder),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1f)
                            )
                            Text(
                                stringResource(Res.string.order_details_change),
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
                Text(stringResource(Res.string.common_continue))
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
                        pickupTime = "${FormatHelper.twoDigits(timePickerState.hour)}:${FormatHelper.twoDigits(timePickerState.minute)}"
                        showTimePicker = false
                    }
                ) { Text(stringResource(Res.string.guest_apply)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(Res.string.common_cancel)) }
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

@Preview
@Composable
private fun OrderDetailsScreenPreview() {
    OrderDetailsScreen(
        restaurantName = "Layali Lounge",
        onConfirmed = { _, _, _ -> },
        deliveryViewModel = DeliveryViewModel(LocationRepositoryMock(), DeliveryPreferencesRepositoryMock())
    )
}
