package com.example.benakmoume_yahi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benakmoume_yahi.models.Recipe
import com.example.benakmoume_yahi.models.Restaurant
import com.example.benakmoume_yahi.remote.RecipeApiService
import com.example.benakmoume_yahi.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val restaurants: List<Restaurant> = emptyList(),
    val error: String? = null,
    val searchQuery: String = ""
)

class SearchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val apiService = RetrofitInstance.api

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isBlank()) {
            // Réinitialiser les résultats si la recherche est vide
            _uiState.value = _uiState.value.copy(
                recipes = emptyList(),
                restaurants = emptyList(),
                error = null
            )
        } else {
            // Lancer la recherche
            searchContent(query)
        }
    }

    private fun searchContent(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Appeler l'API pour rechercher les recettes et restaurants
                //val recipesResponse = apiService.searchRecipes(query)
                val recipesResponse = apiService.getRecipesBySubStr(query)

                //val restaurantsResponse = apiService.searchRestaurants(query)//getRestaurants
                val restaurantsResponse = apiService.searchRestaurantBySubstr(query)//getRestaurants

                val recipes = if (recipesResponse.isSuccessful) {
                    recipesResponse.body() ?: emptyList()
                } else {
                    emptyList()
                }

                val restaurants = if (restaurantsResponse.isSuccessful) {
                    restaurantsResponse.body() ?: emptyList()
                } else {
                    emptyList()
                }

                _uiState.value = _uiState.value.copy(
                    recipes = recipes,
                    restaurants = restaurants,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Une erreur est survenue lors de la recherche"
                )
            }
        }
    }
}
