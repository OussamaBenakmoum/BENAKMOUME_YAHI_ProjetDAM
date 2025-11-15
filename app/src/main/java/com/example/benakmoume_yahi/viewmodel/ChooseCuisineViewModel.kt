package com.example.benakmoume_yahi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benakmoume_yahi.remote.RetrofitInstance
import kotlinx.coroutines.launch

data class ChooseCuisineUiState(
    val areas: List<String> = emptyList(),
    val selectedAreas: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChooseCuisineViewModel : ViewModel() {

    var uiState by mutableStateOf(ChooseCuisineUiState())
        private set

    init {
        loadAreas()
    }

    private fun loadAreas() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = RetrofitInstance.api.getAreas()
                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(
                        areas = response.body()!!.areas,
                        isLoading = false,
                        error = null
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Erreur lors du chargement des cuisines"
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

    fun toggleCuisine(area: String) {
        val current = uiState.selectedAreas
        val next = if (current.contains(area)) current - area else current + area
        uiState = uiState.copy(selectedAreas = next)
    }

    fun clearSelection() {
        uiState = uiState.copy(selectedAreas = emptySet())
    }

    fun setSelection(areas: Set<String>) {
        uiState = uiState.copy(selectedAreas = areas)
    }
}
