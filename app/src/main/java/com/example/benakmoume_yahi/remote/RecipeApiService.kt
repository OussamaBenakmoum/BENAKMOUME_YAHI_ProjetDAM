package com.example.benakmoume_yahi.remote

import com.example.benakmoume_yahi.models.Recipe
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeApiService {

    // GET /recipes/{meal_id}
    @GET("recipes/{meal_id}")
    suspend fun getRecipeById(
        @Path("meal_id") mealId: String
    ): Recipe

    // GET /recipes/random
    @GET("recipes/random")
    suspend fun getRandomRecipe(): Recipe

    // GET /recipes/letter/{letter}
    @GET("recipes/letter/{letter}")
    suspend fun getRecipesByLetter(
        @Path("letter") letter: String
    ): List<Recipe>
}