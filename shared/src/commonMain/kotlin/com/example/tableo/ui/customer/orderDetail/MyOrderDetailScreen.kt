package com.example.tableo.ui.customer.orderDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.tableo.ui.theme.AppShape
import org.koin.compose.viewmodel.koinViewModel
import com.example.tableo.resources.Res
import com.example.tableo.resources.common_back
import com.example.tableo.resources.component_orders_cancel_hint
import com.example.tableo.resources.component_orders_cancel_order
import com.example.tableo.resources.component_orders_reorder
import com.example.tableo.resources.component_orders_track_live
import com.example.tableo.resources.error_with_message
import com.example.tableo.resources.order_detail_title
import org.jetbrains.compose.resources.stringResource

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
                title = { Text(stringResource(Res.string.order_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.common_back))
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
                    Text(stringResource(Res.string.error_with_message, state.message), color = MaterialTheme.colorScheme.error)
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
                        ) { Text(stringResource(Res.string.component_orders_track_live)) }
                    }
                    OutlinedButton(
                        onClick = { onReorder(order) },
                        shape = AppShape.Small,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) { Text(stringResource(Res.string.component_orders_reorder)) }
                    if (canCancel) {
                        TextButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(Res.string.component_orders_cancel_order), color = MaterialTheme.colorScheme.error) }
                    }
                }

                if (showCancelDialog) {
                    CancelOrderDialog(
                        text = stringResource(Res.string.component_orders_cancel_hint, order.restaurantName),
                        onConfirm = { myOrderDetailViewModel.cancelOrder() },
                        onDismiss = { showCancelDialog = false }
                    )
                }
            }
        }
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
