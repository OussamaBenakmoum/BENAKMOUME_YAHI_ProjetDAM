package com.example.benakmoume_yahi.models

data class Review(
    val id: Int,
    val userImageUrl: String,
    val userName: String,
    val textReview: String,
    val reviewAt: String,
    val rating: Int
)