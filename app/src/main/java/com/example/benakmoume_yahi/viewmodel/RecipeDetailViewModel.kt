package com.example.benakmoume_yahi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.benakmoume_yahi.models.Recipe
import com.example.benakmoume_yahi.models.RecipeCommentCreate
import com.example.benakmoume_yahi.models.RecipeCommentWithUser
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

    private val _comments = MutableStateFlow<List<RecipeCommentWithUser>>(emptyList())
    val comments: StateFlow<List<RecipeCommentWithUser>> = _comments.asStateFlow()

    // Charger une recette par ID
    fun loadRecipeById(mealId: String) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val recipe = RetrofitInstance.api.getRecipeById(mealId)
                _uiState.value = RecipeUiState.Success(recipe)
                loadComments(mealId)
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error(
                    message = e.message ?: "Une erreur est survenue"
                )
                _comments.value = emptyList()
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

    suspend fun addRecipeToFavorites(firebaseUid: String, recipeId: String): Boolean {
        val jsonBody = mapOf("id_meal" to recipeId)
        return try {
            val response = RetrofitInstance.api.addFavorite(firebaseUid, jsonBody)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isRecipeFavorite(firebaseUid: String, idMeal: String): Boolean {
        return try {
            val response = RetrofitInstance.api.checkFavoriteStatus(firebaseUid, idMeal)
            response.is_favorite
        } catch (e: Exception) {
            false // En cas d’erreur, considérer que ce n’est pas un favori
        }
    }

    suspend fun removeRecipeFromFavorites(firebaseUid: String, recipeId: String): Boolean {
        return try {
            val response = RetrofitInstance.api.removeFavorite(firebaseUid, recipeId)
            response.message.contains("retirée")
        } catch (e: Exception) {
            false
        }
    }


    suspend fun fetchComments(idMeal: String): List<RecipeCommentWithUser> {
        return try {
            RetrofitInstance.api.getCommentsByRecipe(idMeal)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun loadComments(idMeal: String) {
        viewModelScope.launch {
            _comments.value = fetchComments(idMeal)
        }
    }

    suspend fun postComment(firebaseUid: String, comment: RecipeCommentCreate): Boolean {
        return try {
            val response = RetrofitInstance.api.createComment(firebaseUid, comment)
            response != null // succès si on reçoit une réponse valide
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteComment(firebaseUid: String, commentId: Int): Boolean {
        return try {
            val response = RetrofitInstance.api.deleteComment(firebaseUid, commentId)
            // si pas d'exception, succès
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }


}