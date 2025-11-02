package com.example.benakmoume_yahi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benakmoume_yahi.models.Recipe
import com.example.benakmoume_yahi.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// États possibles de l'UI
sealed class RecipeUiState {
    object Loading : RecipeUiState()
    data class Success(val recipe: Recipe) : RecipeUiState()
    data class Error(val message: String) : RecipeUiState()
}

class RecipeDetailViewModel : ViewModel() {

    // État observable par l'UI
    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    // Charger une recette par ID
    fun loadRecipeById(mealId: String) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val recipe = RetrofitInstance.api.getRecipeById(mealId)
                _uiState.value = RecipeUiState.Success(recipe)
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error(
                    message = e.message ?: "Une erreur est survenue"
                )
            }
        }
    }

    // Charger une recette aléatoire
    fun loadRandomRecipe() {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val recipe = RetrofitInstance.api.getRandomRecipe()
                _uiState.value = RecipeUiState.Success(recipe)
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error(
                    message = e.message ?: "Une erreur est survenue"
                )
            }
        }
    }

    // Charger des recettes par lettre
    fun loadRecipesByLetter(letter: String) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val recipes = RetrofitInstance.api.getRecipesByLetter(letter)
                if (recipes.body() != null && recipes.isSuccessful()) {
                    _uiState.value = RecipeUiState.Success(recipes.body()!!.first())
                } else {
                    _uiState.value = RecipeUiState.Error("Aucune recette trouvée")
                }
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error(
                    message = e.message ?: "Une erreur est survenue"
                )
            }
        }
    }
}