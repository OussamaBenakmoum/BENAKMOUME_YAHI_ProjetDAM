package com.example.benakmoume_yahi.navigation

import android.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart

sealed class AppRoute(val route: String, val title: String, val icon: ImageVector? = null) {
    // Graph routes (si tu veux les utiliser dans NavHost/navigation)
    object AuthGraph : AppRoute("auth", "Auth")
    object MainGraph : AppRoute("main", "Main")

    // Authentication flow
    object Welcome : AppRoute("welcome", "Bienvenue")
    object LoginOrSignUp : AppRoute("login_or_signup", "Authentification")
    object SignIn : AppRoute("sign_in", "Connexion")
    object SignUp : AppRoute("sign_up", "Inscription")
    object ForgotPassword : AppRoute("forgot_password", "Mot de passe oublié")
    object ChooseCuisine : AppRoute("choose_cuisine", "Choisir une cuisine")
    object ChooseCategory : AppRoute("choose_category", "Choisir une catégorie")




    // Main app flow (bottom navigation)
    object Landing : AppRoute("landing", "Accueil", Icons.Filled.Home)
    object Search  : AppRoute("search", "Recherche", Icons.Filled.Search)
    object Cart    : AppRoute("cart", "Panier", Icons.Filled.ShoppingCart)
    object Profile : AppRoute("profile", "Profil", Icons.Filled.Person)

    // Detail screens
    sealed class RestaurantDetail(restaurantId: Int)
        : AppRoute("restaurant_detail/$restaurantId", "Restaurant Détail") {
        companion object {
            const val ROUTE = "restaurant_detail/{restaurantId}"
            fun createRoute(restaurantId: Int) = "restaurant_detail/$restaurantId"
        }
    }
    sealed class RecipeDetail(mealId: Int)
        : AppRoute("recipe_detail/$mealId", "Recette Détail") {
        companion object {
            const val ROUTE = "recipe_detail/{mealId}"
            fun createRoute(mealId: Int) = "recipe_detail/$mealId"
        }
    }
}
