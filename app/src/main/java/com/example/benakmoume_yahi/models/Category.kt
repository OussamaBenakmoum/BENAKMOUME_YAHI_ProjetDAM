package com.example.benakmoume_yahi.models

data class CategoriesResponse(
    val categories: List<String>
)

data class AreasResponse(
    val areas: List<String>
)

// ===== Comments =====
data class RecipeCommentWithUser(
    val id: Int,
    val user_id: Int,
    val firebase_uid: String,
    val id_meal: String,
    val comment_text: String,
    val rating: Int?,           // nullable si optionnel
    val created_at: String?,    // adapter en Instant/Date si nécessaire
    val updated_at: String?,    // adapter en Instant/Date si nécessaire
    val user_firstname: String?,
    val user_lastname: String?,
    val user_photo: String?
)

data class RecipeCommentCreate(
    val id_meal: String,
    val comment_text: String,
    val rating: Int? = null
)

// ===== Users (conformes Swagger) =====
// POST /users
data class UserCreate(
    val firstname: String,
    val lastname: String,
    val email: String,
    val areas_preferred: String?,        // string | null (CSV: "Italian,French")
    val preferred_categories: String?,   // string | null (CSV)
    val photo_profile: String?,          // string | null (URL/base64)
    val firebase_uid: String
)

// PUT /users/{firebase_uid}
data class UserUpdate(
    val firstname: String? = null,
    val lastname: String? = null,
    val areas_preferred: String? = null,
    val preferred_categories: String? = null,
    val photo_profile: String? = null
)

// Réponse commune GET/POST/PUT users
data class UserResponse(
    val firstname: String,
    val lastname: String,
    val email: String,
    val areas_preferred: String?,
    val preferred_categories: String?,
    val photo_profile: String?,
    val id: Int,
    val firebase_uid: String,
    val created_at: String,
    val updated_at: String
)
