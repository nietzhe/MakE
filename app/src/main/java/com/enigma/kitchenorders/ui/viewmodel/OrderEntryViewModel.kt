package com.enigma.kitchenorders.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigma.kitchenorders.data.local.entity.MenuEntity
import com.enigma.kitchenorders.data.local.entity.OrderEntity
import com.enigma.kitchenorders.data.local.entity.OrderItemEntity
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.domain.repository.KitchenRepository
import com.enigma.kitchenorders.util.DateUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface OrderEntryEvent {
    data class Created(val customerName: String) : OrderEntryEvent
    data class Updated(val customerName: String, val deliveryDate: Long) : OrderEntryEvent
    data object EditRejected : OrderEntryEvent
}

class OrderEntryViewModel(private val repository: KitchenRepository) : ViewModel() {

    // Menus
    val activeMenus: StateFlow<List<MenuEntity>> = repository.getActiveMenus()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _deliveryDate = MutableStateFlow(DateUtils.startOfDay())
    val deliveryDate: StateFlow<Long> = _deliveryDate.asStateFlow()

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _cart = MutableStateFlow<List<OrderItemEntity>>(emptyList())
    val cart: StateFlow<List<OrderItemEntity>> = _cart.asStateFlow()

    private val _editingOrder = MutableStateFlow<OrderEntity?>(null)
    val editingOrder: StateFlow<OrderEntity?> = _editingOrder.asStateFlow()

    // Events
    private val _events = Channel<OrderEntryEvent>(Channel.BUFFERED)
    val events: Flow<OrderEntryEvent> = _events.receiveAsFlow()

    fun setCustomerName(name: String) {
        _customerName.value = name
    }

    fun setDeliveryDate(date: Long) {
        _deliveryDate.value = date
    }

    fun addItemToCart(menu: MenuEntity, quantity: Int, notes: String?) {
        val item = OrderItemEntity(
            orderId = 0,
            menuId = menu.id,
            menuNameSnapshot = menu.name,
            quantity = quantity,
            priceSnapshot = menu.currentPrice,
            notes = notes
        )
        _cart.value = _cart.value + item
    }

    fun removeCartItem(index: Int) {
        val current = _cart.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _cart.value = current
        }
    }

    // pre-fills the form from an existing order, saving then updates 
    // that same record.
    fun startEditing(order: OrderWithItems) {
        _editingOrder.value = order.order
        _customerName.value = order.order.customerName
        _deliveryDate.value = order.order.deliveryDate
        _cart.value = order.items
    }

    fun cancelEditing() {
        resetForm()
    }

    fun submitOrder() {
        val name = _customerName.value.trim()
        if (name.isBlank() || _cart.value.isEmpty()) return
        val items = _cart.value
        val date = _deliveryDate.value
        val editing = _editingOrder.value

        viewModelScope.launch {
            try {
                if (editing != null) {
                    val updated = repository.updateOrder(
                        editing.copy(customerName = name, deliveryDate = date),
                        items
                    )
                    resetForm()
                    _events.send(
                        if (updated) OrderEntryEvent.Updated(name, date) else OrderEntryEvent.EditRejected
                    )
                } else {
                    repository.createOrder(customerName = name, deliveryDate = date, items = items)
                    resetForm()
                    _events.send(OrderEntryEvent.Created(name))
                }
            } catch (e: Exception) {
                // pass
            }
        }
    }

    private fun resetForm() {
        _editingOrder.value = null
        _customerName.value = ""
        _cart.value = emptyList()
        _deliveryDate.value = DateUtils.startOfDay()
    }
}
