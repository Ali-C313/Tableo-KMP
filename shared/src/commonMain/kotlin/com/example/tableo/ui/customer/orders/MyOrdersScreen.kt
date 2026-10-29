package com.example.tableo.ui.customer.orders

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.BottomTab
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.ui.components.AppBottomBar
import com.example.tableo.ui.components.MyOrderCard
import com.example.tableo.ui.guest.cart.CartViewModel
import com.example.tableo.util.RequestNotificationPermission
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import com.example.tableo.resources.Res
import com.example.tableo.resources.error_with_message
import com.example.tableo.resources.my_orders_empty
import com.example.tableo.resources.my_orders_title
import org.jetbrains.compose.resources.stringResource

/**
 * Bestellhistorie des eingeloggten Kunden, mit Live-Status.
 * Unten steht die Tab-Leiste mit "Bestellungen" als aktivem Reiter.
 *
 * @param onReorder Wird mit restaurantId + restaurantName aufgerufen, nachdem der Warenkorb
 * mit den Artikeln der ausgewählten Bestellung befüllt wurde
 * @param onTrackOrder Wird mit der Bestell-ID aufgerufen, um eine Lieferung live zu verfolgen
 * @param onOrderClick Wird mit der Bestell-ID aufgerufen, um die Details zu öffnen
 * @param onHomeTabClick Wird aufgerufen, wenn der "Start"-Reiter angetippt wird
 * @param onSettingsTabClick Wird aufgerufen, wenn der "Einstellungen"-Reiter angetippt wird
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    onReorder: (String, String) -> Unit = { _, _ -> },
    onTrackOrder: (String) -> Unit = {},
    onOrderClick: (String) -> Unit = {},
    onHomeTabClick: () -> Unit = {},
    onSettingsTabClick: () -> Unit = {},
    myOrdersViewModel: MyOrdersViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinInject()
) {
    val uiState by myOrdersViewModel.uiState.collectAsState()
    val cancelError by myOrdersViewModel.cancelError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // z.B. "wird schon zubereitet" kurz unten einblenden
    LaunchedEffect(cancelError) {
        cancelError?.let {
            snackbarHostState.showSnackbar(it)
            myOrdersViewModel.clearCancelError()
        }
    }

    LaunchedEffect(Unit) { myOrdersViewModel.loadOrders() }
    RequestNotificationPermission()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.my_orders_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedTab = BottomTab.ORDERS,
                onHomeClick = onHomeTabClick,
                onOrdersClick = {},
                onSettingsClick = onSettingsTabClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is MyOrdersUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is MyOrdersUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.error_with_message, state.message), color = MaterialTheme.colorScheme.error)
                }
            }

            is MyOrdersUiState.Success -> {
                if (state.orders.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Text(stringResource(Res.string.my_orders_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.orders) { order: Order ->
                            MyOrderCard(
                                order = order,
                                onReorderClick = {
                                    cartViewModel.reorder(order)
                                    onReorder(order.restaurantId, order.restaurantName)
                                },
                                onTrackClick = { onTrackOrder(order.id) },
                                onCancelClick = { myOrdersViewModel.cancelOrder(order) },
                                onClick = { onOrderClick(order.id) }
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
private fun MyOrdersScreenPreview() {
    MyOrdersScreen(
        onReorder = { _, _ -> },
        myOrdersViewModel = MyOrdersViewModel(OrderRepositoryMock(), AuthRepositoryMock()),
        cartViewModel = CartViewModel(OrderRepositoryMock(), AuthRepositoryMock())
    )
}