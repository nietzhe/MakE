package com.enigma.kitchenorders

import android.app.Application
import com.enigma.kitchenorders.data.local.AppDatabase
import com.enigma.kitchenorders.data.local.PreferencesManager
import com.enigma.kitchenorders.data.repository.KitchenRepositoryImpl
import com.enigma.kitchenorders.domain.repository.KitchenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class KitchenApp : Application() {

    lateinit var database: AppDatabase
    lateinit var repository: KitchenRepository
    lateinit var preferencesManager: PreferencesManager

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        repository = KitchenRepositoryImpl(database)
        preferencesManager = PreferencesManager(applicationContext)

        // anything still flagged at the next app start is permanently removed.
        appScope.launch { repository.purgeDeletedOrders() }
    }
}
