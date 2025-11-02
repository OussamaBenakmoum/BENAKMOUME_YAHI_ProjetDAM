package com.example.benakmoume_yahi.models

import com.google.gson.annotations.SerializedName

data class Restaurant(
    /*val id: Int,
    val name: String,
    val tags: List<String>,
    val imageRes: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double*/
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String,
    @SerializedName("cuisine_type")
    val cuisineType: String,
    val description: String?,
    val rating: Double?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,

    // Champs pour compatibilité avec votre UI actuelle
    val tags: List<String> = listOf(),
    //val imageRes: Int = android.R.drawable.ic_menu_gallery
    val imageRes: String? = null,

)