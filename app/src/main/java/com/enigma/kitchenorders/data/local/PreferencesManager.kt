package com.enigma.kitchenorders.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val BUSINESS_NAME_KEY = stringPreferencesKey("business_name")
        private val READY_SECTION_EXPANDED_KEY = booleanPreferencesKey("ready_section_expanded")
    }

    /** Whether the "Siap diantar" section on the Dapur page is expanded. Defaults to expanded. */
    val readySectionExpanded: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[READY_SECTION_EXPANDED_KEY] ?: true
    }

    suspend fun setReadySectionExpanded(expanded: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[READY_SECTION_EXPANDED_KEY] = expanded
        }
    }
    
    val businessName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[BUSINESS_NAME_KEY] ?: "Dapur Saya"
    }
    
    suspend fun setBusinessName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[BUSINESS_NAME_KEY] = name
        }
    }
}
