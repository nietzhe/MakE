package com.enigma.kitchenorders.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enigma.kitchenorders.data.local.model.KitchenPreviewItem
import com.enigma.kitchenorders.domain.repository.KitchenRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class MenuAggregation(
    val menuName: String,
    val totalQuantity: Int,
    val details: List<KitchenPreviewItem>
)

class KitchenViewModel(private val repository: KitchenRepository) : ViewModel() {

    private val _selectedDate = MutableStateFlow(getTodayDate())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    private val _filterKeyword = MutableStateFlow("")
    val filterKeyword: StateFlow<String> = _filterKeyword.asStateFlow()

    // raw items from DB
    private val _rawItems = _selectedDate.flatMapLatest { date ->
        repository.getKitchenPreview(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<List<MenuAggregation>> = combine(_rawItems, _filterKeyword) { items, keyword ->
        aggregateItems(items, keyword)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDate(date: Long) {
        _selectedDate.value = date
    }

    fun setFilter(keyword: String) {
        _filterKeyword.value = keyword
    }
    
    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            repository.cancelOrder(orderId)
        }
    }

    private fun aggregateItems(items: List<KitchenPreviewItem>, keyword: String): List<MenuAggregation> {
        val filtered = if (keyword.isBlank()) items else items.filter { 
            (it.notes ?: "").contains(keyword, ignoreCase = true) 
        }

        val grouped = filtered.groupBy { it.menuName }

        return grouped.map { (menuName, itemList) ->
            MenuAggregation(
                menuName = menuName,
                totalQuantity = itemList.sumOf { it.quantity },
                details = itemList
            )
        }
    }

    private fun getTodayDate(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
