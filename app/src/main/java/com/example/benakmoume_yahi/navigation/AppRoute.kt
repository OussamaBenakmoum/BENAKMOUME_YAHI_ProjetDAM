package com.example.benakmoume_yahi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart

sealed class AppRoute(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    // Authentication flow
    object Welcome : AppRoute("welcome", "Bienvenue")
    object LoginOrSignUp : AppRoute("login_or_signup", "Connexion")

    // Main app flow (bottom navigation)
    object Landing : AppRoute("landing", "Accueil", Icons.Filled.Home)
    object Search : AppRoute("search", "Recherche", Icons.Filled.Search)
    object Cart : AppRoute("cart", "Panier", Icons.Filled.ShoppingCart)
    object Profile : AppRoute("profile", "Profil", Icons.Filled.Person)

    // Detail screens within the main flow
    object RestaurantDetail : AppRoute("restaurant_detail", "Restaurant Détail")
    object RecipeDetail : AppRoute("recipe_detail", "Recette Détail")
}