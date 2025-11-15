package com.example.benakmoume_yahi.models

data class FavoriteItem(
    val id: Int,
    val user_id: Int,
    val id_meal: String,
    val created_at: String
)

data class RecipeUi(
    val id_meal: String,
    val name: String = "Recette",
    val image_url: String? = null,
    val area: String? = null,
    val category: String? = null,
    val tags: String? = null
)

data class FavoriteCheckResponse(
    val is_favorite: Boolean
)

data class DeleteFavoriteResponse(
    val message: String
)