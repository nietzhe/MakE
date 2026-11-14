package com.enigma.kitchenorders.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigma.kitchenorders.data.local.PreferencesManager
import com.enigma.kitchenorders.data.local.entity.OrderStatus
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.data.local.model.ProductionTotal
import com.enigma.kitchenorders.domain.repository.KitchenRepository
import com.enigma.kitchenorders.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class OrderDialogKind { DELETE, DONE, REOPEN }

data class PendingOrderAction(
    val kind: OrderDialogKind,
    val orderId: Long,
    val customerName: String
)

sealed interface KitchenEvent {
    data class OrderDeleted(val orderId: Long) : KitchenEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
class KitchenViewModel(
    private val repository: KitchenRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    companion object {
        const val DONE_FLAG_MILLIS = 600L
    }

    private val _selectedDate = MutableStateFlow(DateUtils.startOfDay())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    private val orders: Flow<List<OrderWithItems>> = _selectedDate.flatMapLatest { repository.observeOrdersByDate(it) }

    val activeOrders: StateFlow<List<OrderWithItems>> = orders
        .map { list -> list.filter { it.order.status == OrderStatus.ACTIVE } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val doneOrders: StateFlow<List<OrderWithItems>> = orders
        .map { list -> list.filter { it.order.status == OrderStatus.DONE } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productionTotals: StateFlow<List<ProductionTotal>> = _selectedDate
        .flatMapLatest { repository.observeProductionTotals(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val doneQuantity: StateFlow<Int> = _selectedDate
        .flatMapLatest { repository.observeDoneQuantity(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val readySectionExpanded: StateFlow<Boolean> = preferencesManager.readySectionExpanded
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _expandedOrderIds = MutableStateFlow<Set<Long>>(emptySet())
    val expandedOrderIds: StateFlow<Set<Long>> = _expandedOrderIds.asStateFlow()

    private val _actionMenuOrderId = MutableStateFlow<Long?>(null)
    val actionMenuOrderId: StateFlow<Long?> = _actionMenuOrderId.asStateFlow()

    private val _pendingAction = MutableStateFlow<PendingOrderAction?>(null)
    val pendingAction: StateFlow<PendingOrderAction?> = _pendingAction.asStateFlow()

    private val _celebratingOrderId = MutableStateFlow<Long?>(null)
    val celebratingOrderId: StateFlow<Long?> = _celebratingOrderId.asStateFlow()

    private val _events = Channel<KitchenEvent>(Channel.BUFFERED)
    val events: Flow<KitchenEvent> = _events.receiveAsFlow()

    fun setDate(date: Long) {
        _selectedDate.value = date
        dismissActions()
    }

    fun toggleExpanded(orderId: Long) {
        _expandedOrderIds.value = _expandedOrderIds.value.let { if (orderId in it) it - orderId else it + orderId }
    }

    fun setReadySectionExpanded(expanded: Boolean) {
        viewModelScope.launch { preferencesManager.setReadySectionExpanded(expanded) }
    }

    fun openActions(orderId: Long) {
        _actionMenuOrderId.value = orderId
    }

    fun dismissActions() {
        _actionMenuOrderId.value = null
    }

    fun requestDelete(order: OrderWithItems) = request(OrderDialogKind.DELETE, order)
    fun requestDone(order: OrderWithItems) = request(OrderDialogKind.DONE, order)
    fun requestReopen(order: OrderWithItems) = request(OrderDialogKind.REOPEN, order)

    private fun request(kind: OrderDialogKind, order: OrderWithItems) {
        _pendingAction.value = PendingOrderAction(kind, order.order.id, order.order.customerName)
    }

    fun dismissPendingAction() {
        _pendingAction.value = null
    }

    fun confirmPendingAction() {
        val action = _pendingAction.value ?: return
        _pendingAction.value = null
        _actionMenuOrderId.value = null
        when (action.kind) {
            OrderDialogKind.DELETE -> viewModelScope.launch {
                repository.softDelete(action.orderId)
                _events.send(KitchenEvent.OrderDeleted(action.orderId))
            }

            OrderDialogKind.DONE -> viewModelScope.launch {
                _celebratingOrderId.value = action.orderId
                delay(DONE_FLAG_MILLIS)
                repository.markDone(action.orderId)
                _celebratingOrderId.value = null
            }

            OrderDialogKind.REOPEN -> viewModelScope.launch { repository.reopen(action.orderId) }
        }
    }

    fun undoDelete(orderId: Long) {
        viewModelScope.launch { repository.undoDelete(orderId) }
    }
}
