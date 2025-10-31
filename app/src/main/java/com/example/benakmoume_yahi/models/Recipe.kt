package com.example.benakmoume_yahi.models

data class Recipe(
    val id_meal: String,
    val name: String,
    val category: String? = null,
    val area: String? = null,
    val image_url: String? = null,
    val youtube_url: String? = null,
    val tags: String? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val created_at: String? = null,
    val updated_at: String? = null
)