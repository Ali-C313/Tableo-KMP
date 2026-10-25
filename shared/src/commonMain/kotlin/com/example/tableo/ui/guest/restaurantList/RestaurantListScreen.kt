package com.example.tableo.ui.guest.restaurantList

import com.example.tableo.resources.restaurants_greeting
import com.example.tableo.resources.restaurants_greeting_guest
import com.example.tableo.resources.Res
import com.example.tableo.resources.error_with_message
import com.example.tableo.resources.filter_all
import com.example.tableo.resources.restaurants_search_placeholder
import com.example.tableo.resources.restaurants_title
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.tableo.util.OpeningHoursHelper
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tableo.data.enums.BottomTab
import com.example.tableo.data.enums.DeliveryMode
import com.example.tableo.data.model.Restaurant
import com.example.tableo.data.repository.mock.AuthRepositoryMock
import com.example.tableo.data.repository.mock.DeliveryPreferencesRepositoryMock
import com.example.tableo.data.repository.mock.LocationRepositoryMock
import com.example.tableo.data.repository.mock.RestaurantRepositoryMock
import com.example.tableo.ui.components.AddressDialog
import com.example.tableo.ui.components.AppBottomBar
import com.example.tableo.ui.components.CategoryTile
import com.example.tableo.ui.components.DeliveryHeader
import com.example.tableo.ui.components.RestaurantCard
import com.example.tableo.ui.guest.delivery.AddressUiState
import com.example.tableo.ui.guest.delivery.DeliveryViewModel
import com.example.tableo.ui.theme.TableoTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions

/**
 * Übersicht aller teilnehmenden Restaurants/Shisha-Bars. Startbildschirm für Gäste.
 * Oben stehen Überschrift, Begrüßung und Suchfeld fest, Kategorien, Filter und Liste scrollen gemeinsam.
 * Die Suche findet Restaurants über den Namen und über die Adresse.
 * Ganz oben stehen Lieferadresse und die Auswahl Abholung/Lieferung.
 *
 * @param onRestaurantSelected Wird aufgerufen, wenn der Gast ein Restaurant antippt
 * @param onOrdersTabClick Wird aufgerufen, wenn der "Bestellungen"-Tab angetippt wird
 * @param onSettingsTabClick Wird aufgerufen, wenn der "Konto"-Tab angetippt wird
 */
@Composable
fun RestaurantListScreen(
    onRestaurantSelected: (Restaurant) -> Unit,
    onOrdersTabClick: () -> Unit = {},
    onSettingsTabClick: () -> Unit = {},
    restaurantListViewModel: RestaurantListViewModel = koinViewModel(),
    deliveryViewModel: DeliveryViewModel = koinInject()
) {
    val uiState by restaurantListViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val deliveryMode by deliveryViewModel.deliveryMode.collectAsState()
    val deliveryAddress by deliveryViewModel.address.collectAsState()
    val addressState by deliveryViewModel.addressState.collectAsState()
    var showAddressDialog by remember { mutableStateOf(false) }

    // Dialog schließt sich von selbst, sobald eine Adresse gefunden wurde
    LaunchedEffect(addressState) {
        if (addressState is AddressUiState.Success) {
            showAddressDialog = false
            deliveryViewModel.resetAddressState()
        }
    }
    var searchText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedCuisine by remember { mutableStateOf<String?>(null) }

    val isLoggedIn = restaurantListViewModel.isLoggedIn
    val displayName = restaurantListViewModel.displayName

    Scaffold(
        bottomBar = {
            AppBottomBar(
                selectedTab = BottomTab.HOME,
                onHomeClick = {},
                onOrdersClick = onOrdersTabClick,
                onSettingsClick = onSettingsTabClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Bleibt fest oben: Überschrift, Begrüßung und Suchfeld
            Column(modifier = Modifier.padding(20.dp, 12.dp, 20.dp, 8.dp)) {
                DeliveryHeader(
                    address = deliveryAddress?.text,
                    deliveryMode = deliveryMode,
                    onAddressClick = { showAddressDialog = true },
                    onModeSelected = { deliveryViewModel.setDeliveryMode(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    stringResource(Res.string.restaurants_title),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = if (isLoggedIn && displayName != null) {
                        stringResource(Res.string.restaurants_greeting, displayName)
                    } else {
                        stringResource(Res.string.restaurants_greeting_guest)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text(stringResource(Res.string.restaurants_search_placeholder)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    // Enter schließt nur die Tastatur, gefiltert wird schon beim Tippen
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
                )
            }

            // Ab hier scrollt alles gemeinsam: Kategorien, Filter, Liste
            when (val state = uiState) {
                is RestaurantListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is RestaurantListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(Res.string.error_with_message, state.message), color = MaterialTheme.colorScheme.error)
                    }
                }

                is RestaurantListUiState.Success -> {
                    val cuisines = state.restaurants
                        .flatMap { it.cuisineTypes }
                        .filter { it.isNotBlank() }
                        .distinct()

                    val types = state.restaurants.map { it.type }.filter { it.isNotBlank() }.distinct()

                    val filteredRestaurants = state.restaurants
                        // bei Lieferung nur Restaurants, die auch liefern, Abholung geht überall
                        .filter { deliveryMode != DeliveryMode.LIEFERUNG || it.offersDelivery }
                        .filter { selectedType == null || it.type == selectedType }
                        .filter { selectedCuisine == null || it.cuisineTypes.contains(selectedCuisine) }
                        .filter {
                            it.name.contains(searchText, ignoreCase = true) ||
                                    it.address.contains(searchText, ignoreCase = true)
                        }
                        .sortedByDescending { OpeningHoursHelper.isOpenNow(it.openingTime, it.closingTime) }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (cuisines.isNotEmpty()) {
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(cuisines) { cuisine: String ->
                                        CategoryTile(
                                            label = cuisine,
                                            selected = cuisine == selectedCuisine,
                                            onClick = {
                                                selectedCuisine = if (selectedCuisine == cuisine) null else cuisine
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        if (types.isNotEmpty()) {
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    item {
                                        FilterChip(
                                            selected = selectedType == null,
                                            onClick = { selectedType = null },
                                            label = { Text(stringResource(Res.string.filter_all)) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        )
                                    }
                                    items(types) { type: String ->
                                        FilterChip(
                                            selected = type == selectedType,
                                            onClick = { selectedType = type },
                                            label = { Text(type) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        items(filteredRestaurants) { restaurant: Restaurant ->
                            RestaurantCard(
                                restaurant = restaurant,
                                onClick = { onRestaurantSelected(restaurant) },
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }
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
private fun RestaurantListScreenPreview() {
    TableoTheme {
        RestaurantListScreen(
            onRestaurantSelected = {},
            restaurantListViewModel = RestaurantListViewModel(RestaurantRepositoryMock(), AuthRepositoryMock()),
            deliveryViewModel = DeliveryViewModel(LocationRepositoryMock(), DeliveryPreferencesRepositoryMock())
        )
    }
}
@Preview
@Composable
private fun RestaurantListScreenLoggedOutPreview() {
    TableoTheme {
        RestaurantListScreen(
            onRestaurantSelected = {},
            restaurantListViewModel = RestaurantListViewModel(RestaurantRepositoryMock(), AuthRepositoryMock(loggedIn = false)),
            deliveryViewModel = DeliveryViewModel(LocationRepositoryMock(), DeliveryPreferencesRepositoryMock())
        )
    }
}