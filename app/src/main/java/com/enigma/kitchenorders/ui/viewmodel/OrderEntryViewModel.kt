package com.enigma.kitchenorders.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigma.kitchenorders.data.local.entity.MenuEntity
import com.enigma.kitchenorders.data.local.entity.OrderItemEntity
import com.enigma.kitchenorders.domain.repository.KitchenRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class OrderEntryViewModel(private val repository: KitchenRepository) : ViewModel() {

    // Menus
    val activeMenus: StateFlow<List<MenuEntity>> = repository.getActiveMenus()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Form State
    private val _deliveryDate = MutableStateFlow(getNextDeliveryDate())
    val deliveryDate: StateFlow<Long> = _deliveryDate.asStateFlow()

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _cart = MutableStateFlow<List<OrderItemEntity>>(emptyList())
    val cart: StateFlow<List<OrderItemEntity>> = _cart.asStateFlow()

    // Events
    private val _orderSaved = MutableSharedFlow<String>()
    val orderSaved: SharedFlow<String> = _orderSaved.asSharedFlow()

    fun setCustomerName(name: String) {
        _customerName.value = name
    }

    fun setDeliveryDate(date: Long) {
        _deliveryDate.value = date
    }

    fun addItemToCart(menu: MenuEntity, quantity: Int, notes: String?) {
        val item = OrderItemEntity(
            orderId = 0, // Will be set by DAO
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

    fun submitOrder() {
        val name = _customerName.value
        if (name.isBlank() || _cart.value.isEmpty()) return

        viewModelScope.launch {
            try {
                repository.createOrder(
                    customerName = name,
                    deliveryDate = _deliveryDate.value,
                    items = _cart.value
                )
                val savedName = name
                _customerName.value = ""
                _cart.value = emptyList()
                _orderSaved.emit(savedName)
            } catch (e: Exception) {
                // pass
            }
        }
    }

    private fun getNextDeliveryDate(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
