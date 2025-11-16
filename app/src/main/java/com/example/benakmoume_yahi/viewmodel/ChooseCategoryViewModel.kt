package com.example.benakmoume_yahi.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.benakmoume_yahi.data.PreferencesRepository
import com.example.benakmoume_yahi.remote.RetrofitInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ChooseCategoryUiState(
    val categories: List<String> = emptyList(),
    val selectedCategories: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChooseCategoryViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PreferencesRepository(app)

    var uiState by mutableStateOf(ChooseCategoryUiState())
        private set

    init {
        loadCategories()
        restoreSelection()
    }

    private fun normalize(s: String) = s.trim()

    private fun loadCategories() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = RetrofitInstance.api.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    val normalized = response.body()!!.categories
                        .map(::normalize)
                        .filter { it.isNotEmpty() }
                        .distinct()
                        .sorted()
                    uiState = uiState.copy(categories = normalized, isLoading = false)
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Erreur lors du chargement des catégories")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Erreur inconnue")
            }
        }
    }

    private fun restoreSelection() {
        viewModelScope.launch {
            val saved = prefs.categoriesFlow.first()
            if (saved.isNotEmpty()) {
                uiState = uiState.copy(selectedCategories = saved)
            }
        }
    }

    fun toggleCategory(category: String) {
        val key = normalize(category)
        val current = uiState.selectedCategories
        val next = if (current.contains(key)) current - key else current + key
        uiState = uiState.copy(selectedCategories = next)
    }

    fun persistSelection(onDone: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                prefs.saveCategories(uiState.selectedCategories)
                onDone()
            } catch (e: Exception) {
                onError(e.message ?: "Erreur DataStore")
            }
        }
    }
}
