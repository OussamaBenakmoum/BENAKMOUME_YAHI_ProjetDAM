package com.example.benakmoume_yahi.models

data class RestaurantCardData(
    val id: Int,
    val imageRes: Int,
    val name: String,
    val address: String,
    val tags: List<String>
)