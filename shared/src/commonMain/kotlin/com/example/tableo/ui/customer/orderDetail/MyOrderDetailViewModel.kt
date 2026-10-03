package com.example.tableo.ui.customer.orderDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.tableo.data.model.Order
import com.example.tableo.data.repository.OrderRepository
import com.example.tableo.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Eine Bestellung des Kunden live, damit Status und Fahrer sich auch hier sofort ändern. */
class MyOrderDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val orderId: String = savedStateHandle.toRoute<Route.MyOrderDetail>().orderId

    private val _uiState = MutableStateFlow<MyOrderDetailUiState>(MyOrderDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _cancelError = MutableStateFlow<String?>(null)
    val cancelError = _cancelError.asStateFlow()

    init {
        viewModelScope.launch {
            orderRepository.getOrder(orderId)
                .catch { e -> _uiState.update { MyOrderDetailUiState.Error(e.message ?: "Bestellung konnte nicht geladen werden") } }
                .collect { order ->
                    _uiState.update {
                        if (order != null) MyOrderDetailUiState.Success(order)
                        else MyOrderDetailUiState.Error("Bestellung nicht gefunden")
                    }
                }
        }
    }

    /** Kunde storniert, klappt nur solange die Bestellung noch offen ist. */
    fun cancelOrder() {
        viewModelScope.launch {
            orderRepository.cancelOrder(orderId, byRestaurant = false)
                .onFailure { e -> _cancelError.update { e.message ?: "Stornieren hat nicht geklappt" } }
        }
    }

    fun clearCancelError() {
        _cancelError.update { null }
    }
}

/** Zustand der Detailansicht. */
sealed interface MyOrderDetailUiState {
    data object Loading : MyOrderDetailUiState
    data class Success(val order: Order) : MyOrderDetailUiState
    data class Error(val message: String) : MyOrderDetailUiState
}
