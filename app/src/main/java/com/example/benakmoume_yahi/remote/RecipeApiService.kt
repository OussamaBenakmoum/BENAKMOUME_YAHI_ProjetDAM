package com.example.benakmoume_yahi.remote

import com.example.benakmoume_yahi.models.Recipe
import com.example.benakmoume_yahi.models.Restaurant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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
    ): Response<List<Recipe>>

    @GET("recipes/hasstr/{substr}")
    suspend fun getRecipesBySubStr(
        @Path("substr") substr: String
    ): Response<List<Recipe>>


    @GET("restaurants/search/{substring}")
    suspend fun searchRestaurantBySubstr(
        @Path("substring") substring: String
    ): Response<List<Restaurant>>

    @GET("restaurants")
    suspend fun getRestaurants(): Response<List<Restaurant>>

    @GET("restaurants/{restaurant_id}")
    suspend fun getRestaurantById(@Path("restaurant_id") restaurantId: Int): Response<Restaurant>

}