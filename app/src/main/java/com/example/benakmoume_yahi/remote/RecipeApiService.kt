package com.example.benakmoume_yahi.remote

import com.example.benakmoume_yahi.models.AreasResponse
import com.example.benakmoume_yahi.models.CategoriesResponse
import com.example.benakmoume_yahi.models.DeleteFavoriteResponse
import com.example.benakmoume_yahi.models.FavoriteCheckResponse
import com.example.benakmoume_yahi.models.FavoriteItem
import com.example.benakmoume_yahi.models.Recipe
import com.example.benakmoume_yahi.models.RecipeCommentCreate
import com.example.benakmoume_yahi.models.RecipeCommentWithUser
import com.example.benakmoume_yahi.models.Restaurant
import com.example.benakmoume_yahi.models.UserCreate
import com.example.benakmoume_yahi.models.UserUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecipeApiService {

    // ===== Recipes =====
    @GET("recipes/{meal_id}")
    suspend fun getRecipeById(@Path("meal_id") mealId: String): Recipe

    @GET("recipes/random")
    suspend fun getRandomRecipe(): Recipe

    @GET("recipes/letter/{letter}")
    suspend fun getRecipesByLetter(@Path("letter") letter: String): Response<List<Recipe>>

    @GET("recipes/hasstr/{substr}")
    suspend fun getRecipesBySubStr(@Path("substr") substr: String): Response<List<Recipe>>

    // ===== Restaurants =====
    @GET("restaurants/search/{substring}")
    suspend fun searchRestaurantBySubstr(@Path("substring") substring: String): Response<List<Restaurant>>

    @GET("restaurants")
    suspend fun getRestaurants(): Response<List<Restaurant>>

    @GET("restaurants/{restaurant_id}")
    suspend fun getRestaurantById(@Path("restaurant_id") restaurantId: Int): Response<Restaurant>

    // ===== Taxonomies (catalogue) =====
    @GET("categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("areas")
    suspend fun getAreas(): Response<AreasResponse>

    // ===== Favorites =====
    @POST("users/{firebase_uid}/favorites")
    suspend fun addFavorite(
        @Path("firebase_uid") firebase_uid: String,
        @Body body: Map<String, String> // {"id_meal":"..."}
    ): Response<Unit>

    @GET("users/{firebase_uid}/favorites/{id_meal}/check")
    suspend fun checkFavoriteStatus(
        @Path("firebase_uid") firebaseUid: String,
        @Path("id_meal") idMeal: String
    ): FavoriteCheckResponse

    @DELETE("users/{firebase_uid}/favorites/{id_meal}")
    suspend fun removeFavorite(
        @Path("firebase_uid") firebaseUid: String,
        @Path("id_meal") idMeal: String
    ): DeleteFavoriteResponse

    @GET("users/{firebase_uid}/favorites")
    suspend fun getFavorites(@Path("firebase_uid") uid: String): Response<List<FavoriteItem>>

    @DELETE("users/{firebase_uid}/favorites")
    suspend fun clearFavorites(@Path("firebase_uid") uid: String): Response<Unit>

    // ===== Comments =====
    @GET("recipes/{id_meal}/comments")
    suspend fun getCommentsByRecipe(@Path("id_meal") idMeal: String): List<RecipeCommentWithUser>

    @POST("users/{firebase_uid}/comments")
    suspend fun createComment(
        @Path("firebase_uid") firebaseUid: String,
        @Body comment: RecipeCommentCreate
    ): RecipeCommentWithUser

    @DELETE("users/{firebase_uid}/comments/{comment_id}")
    suspend fun deleteComment(
        @Path("firebase_uid") firebaseUid: String,
        @Path("comment_id") commentId: Int
    ): Response<Void>

    // ===== Users (PUT/GET/POST) =====
    @PUT("users/{firebase_uid}")
    suspend fun updateUser(
        @Path("firebase_uid") uid: String,
        @Body body: UserUpdate
    ): Response<Unit>

    @GET("users/{firebase_uid}")
    suspend fun getUser(@Path("firebase_uid") uid: String): Response<Map<String, Any?>>

    @POST("users")
    suspend fun createUser(@Body body: UserCreate): Response<Unit>
}
