package com.example.tableo.ui.guest.cart

import com.example.tableo.resources.Res
import com.example.tableo.resources.cart_error_no_address
import com.example.tableo.resources.cart_error_no_payment
import com.example.tableo.resources.cart_error_submit
import com.example.tableo.resources.common_guest
import com.example.tableo.util.FormatHelper
import org.jetbrains.compose.resources.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tableo.data.enums.OrderStatus
import com.example.tableo.data.enums.OrderType
import com.example.tableo.data.enums.PaymentMethod
import com.example.tableo.data.model.DeliveryAddress
import com.example.tableo.data.model.MenuItem
import com.example.tableo.data.model.Order
import com.example.tableo.data.model.OrderItem
import com.example.tableo.data.repository.AuthRepository
import com.example.tableo.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Warenkorb für den ganzen Bestellvorgang. Läuft in Koin als single, damit er zwischen den Screens erhalten bleibt. */
class CartViewModel(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<OrderItem>>(emptyList())
    val items = _items.asStateFlow()

    /** Restaurant, zu dem die Artikel im Warenkorb gehören. */
    private var cartRestaurantId: String? = null

    /** Gewählte Bezahlart, null solange noch nichts ausgewählt ist. */
    private val _paymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val paymentMethod = _paymentMethod.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState = _submitState.asStateFlow()

    /** Die zuletzt erfolgreich abgeschickte Bestellung, für die Bestätigungsseite (der Warenkorb ist dann schon leer). */
    private val _lastOrder = MutableStateFlow<Order?>(null)
    val lastOrder = _lastOrder.asStateFlow()

    /** Merkt sich die gewählte Bezahlart. */
    fun selectPaymentMethod(method: PaymentMethod) {
        _paymentMethod.update { method }
        // alten Hinweis "Bitte Bezahlart wählen" wieder ausblenden
        if (_submitState.value is SubmitState.Error) _submitState.update { SubmitState.Idle }
    }

    /** Neuer Artikel, oder Menge +1 falls er schon drin ist. */
    fun addItem(menuItem: MenuItem) {
        startCartFor(menuItem.restaurantId)
        _items.update { current ->
            val existingItem = current.find { it.menuItemId == menuItem.id }
            if (existingItem != null) {
                current.map { orderItem ->
                    if (orderItem.menuItemId == menuItem.id) orderItem.copy(quantity = orderItem.quantity + 1)
                    else orderItem
                }
            } else {
                current + OrderItem(menuItemId = menuItem.id, name = menuItem.name, pricePerItem = menuItem.price, quantity = 1)
            }
        }
    }

    /** Ersetzt den Warenkorb durch die Positionen einer alten Bestellung, für "Erneut bestellen". */
    fun reorder(order: Order) {
        cartRestaurantId = order.restaurantId
        _items.update { order.items }
    }

    /** Liegen noch Artikel von einem anderen Restaurant im Warenkorb, wird er vorher geleert. */
    private fun startCartFor(restaurantId: String) {
        if (cartRestaurantId != restaurantId) {
            _items.update { emptyList() }
            cartRestaurantId = restaurantId
        }
    }

    /** Menge +1. */
    fun incrementQuantity(menuItemId: String) {
        _items.update { current ->
            current.map { orderItem ->
                if (orderItem.menuItemId == menuItemId) orderItem.copy(quantity = orderItem.quantity + 1)
                else orderItem
            }
        }
    }

    /** Menge -1. Bei 0 fliegt der Artikel raus. */
    fun removeItem(menuItemId: String) {
        _items.update { current ->
            current.mapNotNull { orderItem ->
                when {
                    orderItem.menuItemId != menuItemId -> orderItem
                    orderItem.quantity > 1 -> orderItem.copy(quantity = orderItem.quantity - 1)
                    else -> null
                }
            }
        }
    }

    /**
     * Baut aus dem Warenkorb eine Order und speichert sie in Firestore.
     *
     * @param deliveryAddress Lieferadresse, nur bei [OrderType.DELIVERY] nötig
     */
    fun submitOrder(
        restaurantId: String,
        restaurantName: String,
        orderType: OrderType,
        tableNumber: Int?,
        pickupTime: String?,
        deliveryAddress: DeliveryAddress? = null
    ) {
        val payment = _paymentMethod.value
        if (payment == null) {
            viewModelScope.launch { _submitState.update { SubmitState.Error(getString(Res.string.cart_error_no_payment)) } }
            return
        }
        if (orderType == OrderType.DELIVERY && deliveryAddress == null) {
            viewModelScope.launch { _submitState.update { SubmitState.Error(getString(Res.string.cart_error_no_address)) } }
            return
        }
        viewModelScope.launch {
            _submitState.update { SubmitState.Loading }
            val isDelivery = orderType == OrderType.DELIVERY
            // gleicher Name wie auf der Startseite, ohne Konto "Gast"
            val customerName = FormatHelper.displayName(authRepository.currentUserName(), authRepository.currentUserEmail())
                ?: getString(Res.string.common_guest)
            val order = Order(
                restaurantId = restaurantId, restaurantName = restaurantName,
                userId = authRepository.currentUserId(),
                customerName = customerName,
                orderType = orderType, tableNumber = tableNumber, pickupTime = pickupTime,
                deliveryAddress = if (isDelivery) deliveryAddress?.text else null,
                deliveryLocation = if (isDelivery) deliveryAddress?.coordinates else null,
                paymentMethod = payment,
                items = _items.value, status = OrderStatus.OFFEN
            )
            orderRepository.placeOrder(order)
                .onSuccess { newId ->
                    // ID merken, damit man von der Bestätigung aus die Lieferung verfolgen kann
                    _lastOrder.update { order.copy(id = newId) }
                    _items.update { emptyList() }
                    _submitState.update { SubmitState.Success }
                }
                .onFailure { e -> _submitState.update { SubmitState.Error(e.message ?: getString(Res.string.cart_error_submit)) } }
        }
    }

    /** Setzt den Status nach dem Absenden wieder zurück. */
    fun resetSubmitState() {
        _submitState.update { SubmitState.Idle }
    }
}
