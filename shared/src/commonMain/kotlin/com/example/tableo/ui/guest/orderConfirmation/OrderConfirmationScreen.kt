package com.example.tableo.ui.guest.orderConfirmation

import com.example.tableo.resources.Res
import com.example.tableo.resources.order_confirmation_back
import com.example.tableo.resources.order_confirmation_thanks
import com.example.tableo.resources.order_confirmation_title
import com.example.tableo.resources.order_track_delivery
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.ui.guest.cart.CartViewModel
import com.example.tableo.ui.theme.TableoTheme
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.util.RequestNotificationPermission
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource

/**
 * Bestätigung nach erfolgreichem Absenden der Bestellung, mit Bestellübersicht
 * (geschätzte Zeit, Bestellart, Artikel, Summe). Die Bestellung kommt aus
 * [CartViewModel.lastOrder], weil der Warenkorb nach dem Bestellen schon geleert ist.
 * Bei Lieferung gibt es zusätzlich den Button zum Live-Tracking.
 *
 * @param onTrackOrder Wird mit der Bestell-ID aufgerufen, um die Lieferung zu verfolgen
 */
@Composable
fun OrderConfirmationScreen(
    restaurantName: String,
    onBackToStart: () -> Unit,
    onTrackOrder: (String) -> Unit = {},
    cartViewModel: CartViewModel = koinInject()
) {
    val order by cartViewModel.lastOrder.collectAsState()

    // damit der Kunde erfährt, wenn seine Bestellung fertig oder unterwegs ist
    RequestNotificationPermission()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 24.dp)
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary)
        }

        Text(
            text = stringResource(Res.string.order_confirmation_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = stringResource(Res.string.order_confirmation_thanks, restaurantName),            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        order?.let { OrderSummaryCard(it) }

        val currentOrder = order
        if (currentOrder != null && currentOrder.orderType == OrderType.DELIVERY && currentOrder.id.isNotBlank()) {
            OutlinedButton(
                onClick = { onTrackOrder(currentOrder.id) },
                shape = AppShape.Small,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text(stringResource(Res.string.order_track_delivery))
            }
        }

        Button(
            onClick = onBackToStart,
            shape = AppShape.Small,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            Text(stringResource(Res.string.order_confirmation_back))        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderConfirmationScreenPreview() {
    TableoTheme {
        OrderConfirmationScreen(
            restaurantName = "Layali Lounge",
            onBackToStart = {},
            cartViewModel = CartViewModel(OrderRepositoryMock(), AuthRepositoryMock())
        )
    }
}
