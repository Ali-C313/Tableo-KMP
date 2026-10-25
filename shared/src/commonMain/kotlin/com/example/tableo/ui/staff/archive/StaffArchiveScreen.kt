package com.example.tableo.ui.staff.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.util.FormatHelper
import com.example.tableo.util.OrderHelper
import org.koin.compose.viewmodel.koinViewModel
import com.example.tableo.resources.Res
import com.example.tableo.resources.archive_title
import com.example.tableo.resources.error_with_message
import com.example.tableo.resources.archive_empty
import com.example.tableo.resources.common_guest
import com.example.tableo.resources.archive_cancelled_by_restaurant
import com.example.tableo.resources.archive_cancelled_by_customer
import org.jetbrains.compose.resources.stringResource

/** Archiv der erledigten Bestellungen eines Restaurants. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffArchiveScreen(
    onBackClick: () -> Unit = {},
    staffArchiveViewModel: StaffArchiveViewModel = koinViewModel()
) {
    val uiState by staffArchiveViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.archive_title, staffArchiveViewModel.restaurantName),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is StaffArchiveUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is StaffArchiveUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.error_with_message, state.message), color = MaterialTheme.colorScheme.error)
                }
            }

            is StaffArchiveUiState.Success -> {
                if (state.orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Text(stringResource(Res.string.archive_empty), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.orders) { order: Order ->
                            val orderTypeText = OrderHelper.orderTypeLabel(order)
                            val guest = stringResource(Res.string.common_guest)
                            Card(
                                shape = AppShape.Medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Card)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                    Text(orderTypeText, style = MaterialTheme.typography.titleMedium)
                                    if (order.status == OrderStatus.STORNIERT) {
                                        Text(
                                            text = if (order.cancelledByRestaurant) {
                                                stringResource(Res.string.archive_cancelled_by_restaurant)
                                            } else {
                                                stringResource(Res.string.archive_cancelled_by_customer)
                                            },
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    Text(
                                        text = "${OrderHelper.orderNumber(order)} · ${order.customerName.ifBlank { guest }} · ${FormatHelper.dateTime(order.createdAt)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = order.items.joinToString(", ") { "${it.quantity}× ${it.name}" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    if (!order.deliveryAddress.isNullOrBlank()) {
                                        Text(
                                            text = order.deliveryAddress,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = FormatHelper.euro(order.totalPrice),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Preview
@Composable
private fun StaffArchiveScreenPreview() {
    val savedStateHandle =
        SavedStateHandle(mapOf("restaurantId" to "1", "restaurantName" to "Layali Lounge"))
    StaffArchiveScreen(
        staffArchiveViewModel = StaffArchiveViewModel(savedStateHandle, OrderRepositoryMock())
    )
}