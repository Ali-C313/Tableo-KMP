package com.example.tableo.ui.guest.menu

import com.example.tableo.resources.Res
import com.example.tableo.resources.common_back
import com.example.tableo.resources.common_continue
import com.example.tableo.resources.error_with_message
import com.example.tableo.resources.filter_all
import com.example.tableo.resources.menu_cart_items
import com.example.tableo.resources.menu_currency_placeholder
import com.example.tableo.util.FormatHelper
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.model.MenuItem
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.ExchangeRateRepositoryMock
import com.example.tableo.data.repository.mock.MenuRepositoryMock
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.ui.components.DropdownPicker
import com.example.tableo.ui.components.MenuItemCard
import com.example.tableo.ui.guest.cart.CartViewModel
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

private val DisplayCurrencies = listOf("EUR", "USD", "GBP", "TRY")

/**
 * Speisekarte eines Restaurants. Kategorien werden dynamisch aus den geladenen
 * Menü-Einträgen ermittelt, statt fest im Code zu stehen. Restaurant-ID und -Name
 * kommen über die Route direkt ins ViewModel, nicht mehr als Parameter hierher.
 *
 * @param onBackClick Wird aufgerufen, wenn der Zurück-Pfeil angetippt wird
 * @param onContinueClick Wird aufgerufen, wenn der Gast mit dem Warenkorb weitergehen will
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    menuViewModel: MenuViewModel = koinViewModel(),
    exchangeRateViewModel: ExchangeRateViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinInject()
) {
    val uiState by menuViewModel.uiState.collectAsState()
    val exchangeState by exchangeRateViewModel.uiState.collectAsState()
    val cartItems by cartViewModel.items.collectAsState()
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedCurrency by remember { mutableStateOf("EUR") }

    LaunchedEffect(Unit) {
        exchangeRateViewModel.loadRates()
    }

    val exchangeRate: Double? = if (selectedCurrency == "EUR") {
        null
    } else {
        (exchangeState as? ExchangeRateUiState.Success)?.rates?.get(selectedCurrency)
    }

    val cartCount = cartItems.sumOf { it.quantity }
    val cartTotal = cartItems.sumOf { it.subtotal }
    val categories: List<String> = (uiState as? MenuUiState.Success)
        ?.items
        ?.map { menuItem: MenuItem -> menuItem.category }
        ?.filter { category: String -> category.isNotBlank() }
        ?.distinct()
        ?.sorted()
        ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = menuViewModel.restaurantName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.common_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (cartCount > 0) {
                Card(
                    modifier = Modifier.navigationBarsPadding(),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Floating)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Column {
                                Text(
                                    stringResource(Res.string.menu_cart_items, cartCount),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = FormatHelper.euro(cartTotal),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Button(
                            onClick = onContinueClick,
                            shape = AppShape.Small
                        ) {
                            Text(stringResource(Res.string.common_continue))
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text(stringResource(Res.string.filter_all)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                    items(categories) { category: String ->
                        FilterChip(
                            selected = category == selectedCategory,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                DropdownPicker(
                    selected = selectedCurrency,
                    options = DisplayCurrencies,
                    onSelected = { selectedCurrency = it },
                    placeholder = stringResource(Res.string.menu_currency_placeholder)
                )
            }

            when (val state = uiState) {
                is MenuUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is MenuUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(Res.string.error_with_message, state.message), color = MaterialTheme.colorScheme.error)
                    }
                }

                is MenuUiState.Success -> {
                    val filteredItems: List<MenuItem> = state.items.filter { menuItem: MenuItem ->
                        selectedCategory == null || menuItem.category == selectedCategory
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp, 4.dp, 16.dp, 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredItems) { menuItem: MenuItem ->
                            val quantity = cartItems.find { it.menuItemId == menuItem.id }?.quantity ?: 0
                            val convertedText = exchangeRate?.let { FormatHelper.foreignCurrency(menuItem.price * it, selectedCurrency) }
                            MenuItemCard(
                                menuItem = menuItem,
                                imageUrl = menuItem.imageUrl,
                                cartQuantity = quantity,
                                convertedPriceText = convertedText,
                                onAddClick = { cartViewModel.addItem(menuItem) }
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
private fun MenuScreenPreview() {
    val savedStateHandle =
        SavedStateHandle(mapOf("restaurantId" to "1", "restaurantName" to "Layali Lounge"))
    MenuScreen(
        menuViewModel = MenuViewModel(savedStateHandle, MenuRepositoryMock()),
        exchangeRateViewModel = ExchangeRateViewModel(ExchangeRateRepositoryMock()),
        cartViewModel = CartViewModel(OrderRepositoryMock(), AuthRepositoryMock())
    )
}