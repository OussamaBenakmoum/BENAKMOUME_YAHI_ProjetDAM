package com.example.benakmoume_yahi.data

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class PreferencesRepository(private val context: Context) {

    private companion object {
        val KEY_CUISINES: Preferences.Key<Set<String>> =
            stringSetPreferencesKey("areas_preferred_set")
        val KEY_CATEGORIES: Preferences.Key<Set<String>> =
            stringSetPreferencesKey("categories_preferred_set")
    }

    // Flows de lecture
    val cuisinesFlow: Flow<Set<String>> =
        context.dataStore.data.map { prefs: Preferences -> prefs[KEY_CUISINES] ?: emptySet() }

    val categoriesFlow: Flow<Set<String>> =
        context.dataStore.data.map { prefs: Preferences -> prefs[KEY_CATEGORIES] ?: emptySet() }

    // Écriture cuisines (Set<String>)
    suspend fun saveCuisines(cuisines: Set<String>) {
        val clean = cuisines.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        context.dataStore.edit { mp: MutablePreferences ->
            mp[KEY_CUISINES] = clean
        }
    }

    // Écriture catégories (Set<String>)
    suspend fun saveCategories(categories: Set<String>) {
        val clean = categories.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        context.dataStore.edit { mp: MutablePreferences ->
            mp[KEY_CATEGORIES] = clean
        }
    }
}
