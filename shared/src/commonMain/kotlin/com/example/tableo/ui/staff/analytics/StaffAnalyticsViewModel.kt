package com.example.tableo.ui.staff.analytics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tableo.data.enums.AnalyticsPeriod
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.model.ChartBucket
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.tableo.util.currentTimeMillis
import com.example.tableo.util.formatDateTime
import com.example.tableo.resources.Res
import com.example.tableo.resources.analytics_error_load
import org.jetbrains.compose.resources.getString

/** Rechnet Umsatz und Bestellanzahl für den gewählten Zeitraum aus. */
class StaffAnalyticsViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Route.StaffAnalytics>()
    private val restaurantId: String = route.restaurantId
    val restaurantName: String = route.restaurantName

    private val _uiState = MutableStateFlow<StaffAnalyticsUiState>(StaffAnalyticsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _period = MutableStateFlow(AnalyticsPeriod.LAST_24H)
    val period = _period.asStateFlow()

    // hält alle abgeschlossenen Bestellungen im Speicher, damit beim Wechsel des
    // Zeitraums keine neue Firestore-Abfrage nötig ist, nur neu gerechnet wird
    private var allCompletedOrders: List<Order> = emptyList()

    init {
        viewModelScope.launch {
            orderRepository.getOrderHistory(restaurantId)
                .catch { e -> _uiState.update { StaffAnalyticsUiState.Error(e.message ?: getString(Res.string.analytics_error_load)) } }
                .collect { orders ->
                    // stornierte Bestellungen bringen keinen Umsatz
                    allCompletedOrders = orders.filter { it.status != OrderStatus.STORNIERT }
                    recompute()
                }
        }
    }

    /** Wechselt den angezeigten Zeitraum und berechnet die Balken neu. */
    fun selectPeriod(newPeriod: AnalyticsPeriod) {
        _period.update { newPeriod }
        recompute()
    }

    private fun recompute() {
        val selectedPeriod = _period.value
        val now = currentTimeMillis()
        val start = now - selectedPeriod.millis
        val relevantOrders = allCompletedOrders.filter { it.createdAt in start..now }

        val bucketSizeMillis = selectedPeriod.millis / selectedPeriod.bucketCount
        val buckets = (0 until selectedPeriod.bucketCount).map { index ->
            val bucketStart = start + index * bucketSizeMillis
            val bucketEnd = bucketStart + bucketSizeMillis
            val bucketOrders = relevantOrders.filter { it.createdAt in bucketStart until bucketEnd }
            ChartBucket(
                label = formatBucketLabel(bucketStart, selectedPeriod),
                revenue = bucketOrders.sumOf { it.totalPrice },
                orderCount = bucketOrders.size
            )
        }

        _uiState.update {
            StaffAnalyticsUiState.Success(
                buckets = buckets,
                totalRevenue = relevantOrders.sumOf { it.totalPrice },
                totalOrders = relevantOrders.size
            )
        }
    }

    private fun formatBucketLabel(millis: Long, forPeriod: AnalyticsPeriod): String {
        val pattern = when (forPeriod) {
            AnalyticsPeriod.LAST_24H -> "HH:mm"
            AnalyticsPeriod.LAST_3D -> "EEE"
            AnalyticsPeriod.LAST_MONTH -> "dd.MM."
            AnalyticsPeriod.LAST_6_MONTHS -> "MMM"
        }
        return formatDateTime(millis, pattern)
    }
}
