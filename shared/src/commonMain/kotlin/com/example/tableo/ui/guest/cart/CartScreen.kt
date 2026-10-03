package com.example.tableo.ui.guest.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.enums.PaymentMethod
import com.example.tableo.data.model.OrderItem
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.LocationRepositoryMock
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.ui.components.CartItemRow
import com.example.tableo.ui.components.DropdownPicker
import com.example.tableo.ui.guest.delivery.DeliveryViewModel
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.util.asEuro
import org.koin.compose.koinInject

/**
 * Warenkorb-Übersicht: Menge wird hier angepasst, zeigt die gewählte Bestellart, unten wird
 * die Bezahlart gewählt und die Bestellung an Firestore gesendet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    restaurantId: String,
    restaurantName: String,
    orderType: OrderType,
    tableNumber: Int?,
    pickupTime: String?,
    onOrderPlaced: () -> Unit,
    onBackClick: () -> Unit = {},
    cartViewModel: CartViewModel = koinInject(),
    deliveryViewModel: DeliveryViewModel = koinInject()
) {
    val cartItems by cartViewModel.items.collectAsState()
    val submitState by cartViewModel.submitState.collectAsState()
    val paymentMethod by cartViewModel.paymentMethod.collectAsState()
    val deliveryAddress by deliveryViewModel.address.collectAsState()

    LaunchedEffect(submitState) {
        if (submitState is SubmitState.Success) {
            cartViewModel.resetSubmitState()
            onOrderPlaced()
        }
    }

    val totalPrice = cartItems.sumOf { it.subtotal }
    val orderTypeText = when (orderType) {
        OrderType.DINE_IN -> "Tisch $tableNumber"
        OrderType.PICKUP -> "Abholung um $pickupTime Uhr"
        OrderType.DELIVERY -> "Lieferung an ${deliveryAddress?.text.orEmpty()}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Warenkorb") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = restaurantName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = orderTypeText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Dein Warenkorb ist leer.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cartItems) { orderItem: OrderItem ->
                        CartItemRow(
                            orderItem = orderItem,
                            onIncrement = { cartViewModel.incrementQuantity(orderItem.menuItemId) },
                            onDecrement = { cartViewModel.removeItem(orderItem.menuItemId) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bezahlart", style = MaterialTheme.typography.titleMedium)
                    DropdownPicker(
                        selected = paymentMethod,
                        options = PaymentMethod.entries,
                        onSelected = { cartViewModel.selectPaymentMethod(it) },
                        placeholder = "Auswählen",
                        optionLabel = { it.label }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Gesamt", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = totalPrice.asEuro(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (submitState is SubmitState.Error) {
                    Text(
                        text = "Fehler: ${(submitState as SubmitState.Error).message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        cartViewModel.submitOrder(
                            restaurantId, restaurantName, orderType, tableNumber, pickupTime, deliveryAddress
                        )
                    },
                    enabled = submitState !is SubmitState.Loading && paymentMethod != null,
                    shape = AppShape.Small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    if (submitState is SubmitState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text("Bestellung senden")
                }
            }
        }
    }
}
@Preview
@Composable
private fun CartScreenPreview() {
    CartScreen(
        restaurantId = "layali-lounge",
        restaurantName = "Layali Lounge",
        orderType = OrderType.DINE_IN,
        tableNumber = 1,
        pickupTime = "12:00",
        onOrderPlaced = {},
        cartViewModel = CartViewModel(OrderRepositoryMock(), AuthRepositoryMock()),
        deliveryViewModel = DeliveryViewModel(LocationRepositoryMock())
    )
}