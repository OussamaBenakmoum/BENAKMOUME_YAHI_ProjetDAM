package com.example.benakmoume_yahi.models

data class CategoriesResponse(val categories: List<String>)
data class AreasResponse(val areas: List<String>)

// ===== Comments =====
data class RecipeCommentWithUser(
    val id: Int,
    val user_id: Int,
    val firebase_uid: String,
    val id_meal: String,
    val comment_text: String,
    val rating: Int?,
    val created_at: String?,
    val updated_at: String?,
    val user_firstname: String?,
    val user_lastname: String?,
    val user_photo: String?
)

data class RecipeCommentCreate(
    val id_meal: String,
    val comment_text: String,
    val rating: Int? = null
)

// ===== Users (CSV) =====
data class UserCreate(
    val firstname: String,
    val lastname: String,
    val email: String,
    val areas_preferred: String?,        // CSV ou null
    val preferred_categories: String?,   // CSV ou null
    val photo_profile: String?,          // URL/base64 ou null
    val firebase_uid: String
)

data class UserUpdate(
    val firstname: String? = null,
    val lastname: String? = null,
    val areas_preferred: String? = null,
    val preferred_categories: String? = null,
    val photo_profile: String? = null
)

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
