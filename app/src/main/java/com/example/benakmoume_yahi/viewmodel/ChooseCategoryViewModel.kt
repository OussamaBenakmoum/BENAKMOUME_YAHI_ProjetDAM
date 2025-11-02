package com.example.benakmoume_yahi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benakmoume_yahi.remote.RetrofitInstance
import kotlinx.coroutines.launch

class ChooseCategoryViewModel : ViewModel() {

    var uiState by mutableStateOf(ChooseCategoryUiState())
        private set

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                val response = RetrofitInstance.api.getCategories()

                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(
                        categories = response.body()!!.categories,
                        isLoading = false,
                        error = null
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Erreur lors du chargement des catégories"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Erreur inconnue"
                )
            }
        }
    }

    fun toggleCategory(category: String) {
        val newSelected = if (uiState.selectedCategories.contains(category)) {
            uiState.selectedCategories - category
        } else {
            uiState.selectedCategories + category
        }
        uiState = uiState.copy(selectedCategories = newSelected)
    }
}

data class ChooseCategoryUiState(
    val categories: List<String> = emptyList(),
    val selectedCategories: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)
