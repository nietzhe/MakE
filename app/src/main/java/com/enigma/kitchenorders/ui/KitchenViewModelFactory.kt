package com.enigma.kitchenorders.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.enigma.kitchenorders.data.local.PreferencesManager
import com.enigma.kitchenorders.domain.repository.KitchenRepository
import com.enigma.kitchenorders.ui.viewmodel.OrderEntryViewModel
import com.enigma.kitchenorders.ui.viewmodel.KitchenViewModel
import com.enigma.kitchenorders.ui.viewmodel.SettingsViewModel

class KitchenViewModelFactory(
    private val repository: KitchenRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderEntryViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(KitchenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KitchenViewModel(repository, preferencesManager) as T
        }
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
