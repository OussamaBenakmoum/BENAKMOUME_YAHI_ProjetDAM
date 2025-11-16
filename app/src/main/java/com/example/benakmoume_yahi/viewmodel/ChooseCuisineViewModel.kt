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

data class ChooseCuisineUiState(
    val areas: List<String> = emptyList(),
    val selectedAreas: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChooseCuisineViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PreferencesRepository(app)

    var uiState by mutableStateOf(ChooseCuisineUiState())
        private set

    init {
        loadAreas()           // depuis API
        restoreSelection()    // depuis DataStore
    }

    private fun normalizeLabel(label: String): String = label.trim()

    private fun loadAreas() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val response = RetrofitInstance.api.getAreas()
                if (response.isSuccessful && response.body() != null) {
                    val normalized = response.body()!!.areas
                        .map(::normalizeLabel)
                        .filter { it.isNotEmpty() }
                        .distinct()
                        .sorted()
                    uiState = uiState.copy(
                        areas = normalized,
                        isLoading = false,
                        error = null
                    )
                } else {
                    uiState = uiState.copy(isLoading = false, error = "Erreur lors du chargement des cuisines")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Erreur inconnue")
            }
        }
    }

    fun toggleCuisine(area: String) {
        val key = normalizeLabel(area)
        val current = uiState.selectedAreas
        val next = if (current.contains(key)) current - key else current + key
        uiState = uiState.copy(selectedAreas = next)
    }

    fun clearSelection() {
        uiState = uiState.copy(selectedAreas = emptySet())
    }

    fun setSelection(areas: Set<String>) {
        val normalized = areas.map(::normalizeLabel).filter { it.isNotEmpty() }.toSet()
        uiState = uiState.copy(selectedAreas = normalized)
    }

    // DataStore — restaurer
    private fun restoreSelection() {
        viewModelScope.launch {
            val saved = prefs.cuisinesFlow.first()
            if (saved.isNotEmpty()) {
                uiState = uiState.copy(selectedAreas = saved)
            }
        }
    }

    // DataStore — sauvegarder
    fun persistSelection(onDone: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                prefs.saveCuisines(uiState.selectedAreas)
                onDone()
            } catch (e: Exception) {
                onError(e.message ?: "Erreur DataStore")
            }
        }
    }

    fun selectedCsvOrNull(): String? =
        uiState.selectedAreas.map { it.trim() }.filter { it.isNotEmpty() }.joinToString(",").ifBlank { null }

    fun readyToSubmit(): Boolean =
        !uiState.isLoading && uiState.error == null && uiState.selectedAreas.isNotEmpty()
}
