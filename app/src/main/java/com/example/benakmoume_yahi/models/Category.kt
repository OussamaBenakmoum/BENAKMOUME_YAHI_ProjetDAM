package com.example.benakmoume_yahi.models

data class CategoriesResponse(
    val categories: List<String>
)

data class AreasResponse(
    val areas: List<String>
)


data class FavoriteCheckResponse(
    val is_favorite: Boolean
)

data class DeleteFavoriteResponse(
    val message: String
)


data class RecipeCommentWithUser(
    val id: Int,
    val user_id: Int,
    val id_meal: String,
    val comment_text: String,
    val rating: Int?, // nullable si optionnel
    val created_at: String?, // ou Instant/Date selon format
    val updated_at: String?, // ou Instant/Date
    val user_firstname: String?,
    val user_lastname: String?,
    val user_photo: String?
)