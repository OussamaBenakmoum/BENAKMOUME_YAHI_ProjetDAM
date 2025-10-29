package com.example.benakmoume_yahi.models

data class Restaurant(
    val id: Int,
    val name: String,
    val tags: List<String>,
    val imageRes: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double
)