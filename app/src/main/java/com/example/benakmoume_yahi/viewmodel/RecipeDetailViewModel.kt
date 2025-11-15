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

sealed class RecipeUiState {
    object Loading : RecipeUiState()
    data class Success(val recipe: Recipe) : RecipeUiState()
    data class Error(val message: String) : RecipeUiState()
}

class RecipeDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    private val _comments = MutableStateFlow<List<RecipeCommentWithUser>>(emptyList())
    val comments: StateFlow<List<RecipeCommentWithUser>> = _comments.asStateFlow()

    fun loadRecipeById(mealId: String) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val recipe = RetrofitInstance.api.getRecipeById(mealId)
                _uiState.value = RecipeUiState.Success(recipe)
                loadComments(mealId)
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error(e.message ?: "Une erreur est survenue")
                _comments.value = emptyList()
            }
        }
    }

    fun loadRandomRecipe() {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val recipe = RetrofitInstance.api.getRandomRecipe()
                _uiState.value = RecipeUiState.Success(recipe)
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error(e.message ?: "Une erreur est survenue")
            }
        }
    }

    fun loadRecipesByLetter(letter: String) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val recipes = RetrofitInstance.api.getRecipesByLetter(letter)
                if (recipes.isSuccessful && !recipes.body().isNullOrEmpty()) {
                    _uiState.value = RecipeUiState.Success(recipes.body()!!.first())
                } else {
                    _uiState.value = RecipeUiState.Error("Aucune recette trouvée")
                }
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error(e.message ?: "Une erreur est survenue")
            }
        }
    }

    // Favorites
    suspend fun addRecipeToFavorites(firebaseUid: String, recipeId: String): Boolean {
        val body: Map<String, String> = mapOf("id_meal" to recipeId)
        return try {
            val resp = RetrofitInstance.api.addFavorite(firebaseUid, body)
            resp.isSuccessful // 200 avec JSON {id, user_id, id_meal, created_at}
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isRecipeFavorite(firebaseUid: String, idMeal: String): Boolean {
        return try {
            val resp = RetrofitInstance.api.checkFavoriteStatus(firebaseUid, idMeal)
            resp.is_favorite
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeRecipeFromFavorites(firebaseUid: String, recipeId: String): Boolean {
        return try {
            val resp = RetrofitInstance.api.removeFavorite(firebaseUid, recipeId)
            // Idéalement l’API renvoie {success:true}; sinon, parsage du message comme fallback
            resp.message.contains("success", ignoreCase = true) ||
                    resp.message.contains("retir", ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }

    // Comments
    suspend fun fetchComments(idMeal: String): List<RecipeCommentWithUser> {
        return try {
            RetrofitInstance.api.getCommentsByRecipe(idMeal)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun loadComments(idMeal: String) {
        viewModelScope.launch { _comments.value = fetchComments(idMeal) }
    }

    suspend fun postComment(firebaseUid: String, comment: RecipeCommentCreate): Boolean {
        return try {
            val resp = RetrofitInstance.api.createComment(firebaseUid, comment)
            resp != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteComment(firebaseUid: String, commentId: Int): Boolean {
        return try {
            val resp = RetrofitInstance.api.deleteComment(firebaseUid, commentId)
            resp.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
