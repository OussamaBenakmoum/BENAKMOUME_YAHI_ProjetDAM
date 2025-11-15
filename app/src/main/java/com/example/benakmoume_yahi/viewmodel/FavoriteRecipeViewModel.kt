package com.example.benakmoume_yahi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benakmoume_yahi.models.FavoriteItem
import com.example.benakmoume_yahi.models.Recipe
import com.example.benakmoume_yahi.remote.RetrofitInstance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val loading: Boolean = false,
    val error: String? = null
)

class FavoritesViewModel : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _ui = MutableStateFlow(FavoritesUiState())
    val ui: StateFlow<FavoritesUiState> = _ui.asStateFlow()

    fun loadFavorites(uid: String) = viewModelScope.launch {
        _ui.value = FavoritesUiState(loading = true)
        try {
            val favResp = RetrofitInstance.api.getFavorites(uid) // List<FavoriteItem>
            if (!favResp.isSuccessful) {
                _ui.value = FavoritesUiState(error = "Erreur chargement favoris: ${favResp.code()}")
                return@launch
            }
            val favoriteItems: List<FavoriteItem> = favResp.body().orEmpty()
            if (favoriteItems.isEmpty()) {
                _recipes.value = emptyList()
                _ui.value = FavoritesUiState()
                return@launch
            }

            // Enrichissement: fetch Recipe pour chaque id_meal en parallèle contrôlée
            val deferred = favoriteItems.map { fav ->
                async {
                    runCatching { RetrofitInstance.api.getRecipeById(fav.id_meal) }.getOrNull()
                }
            }
            val recipes = deferred.awaitAll().filterNotNull()

            _recipes.value = recipes
            _ui.value = FavoritesUiState()
        } catch (e: Exception) {
            _ui.value = FavoritesUiState(error = e.message ?: "Erreur réseau favoris")
        }
    }

    fun clearFavorites(uid: String) = viewModelScope.launch {
        _ui.value = _ui.value.copy(loading = true, error = null)
        try {
            val resp = RetrofitInstance.api.clearFavorites(uid)
            if (resp.isSuccessful) {
                _recipes.value = emptyList()
                _ui.value = FavoritesUiState()
            } else {
                _ui.value = FavoritesUiState(error = "Impossible de vider les favoris")
            }
        } catch (e: Exception) {
            _ui.value = FavoritesUiState(error = e.message ?: "Erreur réseau")
        }
    }

    fun removeLocal(idMeal: String) {
        _recipes.value = _recipes.value.filterNot { it.id_meal == idMeal }
    }

    fun addLocal(recipe: Recipe) {
        if (_recipes.value.any { it.id_meal == recipe.id_meal }) return
        _recipes.value = _recipes.value + recipe
    }
}
