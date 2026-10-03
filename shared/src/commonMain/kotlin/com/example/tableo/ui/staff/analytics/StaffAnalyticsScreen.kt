package com.example.tableo.ui.staff.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import com.example.tableo.data.enums.AnalyticsPeriod
import com.example.tableo.data.repository.mock.OrderRepositoryMock
import com.example.tableo.ui.components.BarChart
import com.example.tableo.ui.theme.AppElevation
import com.example.tableo.ui.theme.AppShape
import com.example.tableo.ui.theme.AppSpacing
import com.example.tableo.ui.util.asEuro
import org.koin.compose.viewmodel.koinViewModel

/** Statistik-Übersicht: abgeschlossene Bestellungen über einen wählbaren Zeitraum. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffAnalyticsScreen(
    onBackClick: () -> Unit = {},
    staffAnalyticsViewModel: StaffAnalyticsViewModel = koinViewModel()
) {
    val uiState by staffAnalyticsViewModel.uiState.collectAsState()
    val selectedPeriod by staffAnalyticsViewModel.period.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistik · ${staffAnalyticsViewModel.restaurantName}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },                navigationIcon = {
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
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = AppSpacing.L, vertical = AppSpacing.M),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
            ) {
                items(AnalyticsPeriod.entries) { periodOption ->
                    FilterChip(
                        selected = periodOption == selectedPeriod,
                        onClick = { staffAnalyticsViewModel.selectPeriod(periodOption) },
                        label = { Text(periodOption.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            when (val state = uiState) {
                is StaffAnalyticsUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is StaffAnalyticsUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Fehler: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }

                is StaffAnalyticsUiState.Success -> {
                    Column(modifier = Modifier.padding(horizontal = AppSpacing.L)) {
                        Card(
                            shape = AppShape.Medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.Card)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(AppSpacing.M),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        state.totalRevenue.asEuro(),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "Umsatz",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${state.totalOrders}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "Bestellungen",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (state.totalOrders == 0) {
                            Text(
                                "In diesem Zeitraum keine abgeschlossenen Bestellungen.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = AppSpacing.L)
                            )
                        } else {
                            BarChart(
                                buckets = state.buckets,
                                modifier = Modifier.padding(top = AppSpacing.XL)
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
private fun StaffAnalyticsScreenPreview() {
    val savedStateHandle =
        SavedStateHandle(mapOf("restaurantId" to "1", "restaurantName" to "Layali Lounge"))
    StaffAnalyticsScreen(
        staffAnalyticsViewModel = StaffAnalyticsViewModel(savedStateHandle, OrderRepositoryMock())
    )
}