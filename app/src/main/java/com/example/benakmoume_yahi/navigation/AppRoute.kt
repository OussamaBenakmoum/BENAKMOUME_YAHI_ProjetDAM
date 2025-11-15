package com.example.benakmoume_yahi.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart

sealed class AppRoute(val route: String, val title: String, val icon: ImageVector? = null) {

    // ========= Auth flow =========
    object Welcome : AppRoute("welcome", "Bienvenue")
    object LoginOrSignUp : AppRoute("login_or_signup", "Authentification")
    object GoogleSignUp : AppRoute("google_signup", "Compléter profil")

    object SignUp : AppRoute("SignUp", "Inscription")
    object SignIn : AppRoute("SignIn", "Connexion")
    object ForgotPassword : AppRoute("ForgotPassword", "Mot de passe Oublié")

    // ========= Préférences (onboarding / profil) =========
    object ChooseCuisine : AppRoute("choose_cuisine", "Choisir une cuisine")

    // Paramétrée pour porter le contexte: FROM_SIGNUP ou FROM_PROFILE
    object ChooseCategory : AppRoute("choose_category?from={from}", "Choisir une catégorie") {
        const val FROM_SIGNUP = "FROM_SIGNUP"
        const val FROM_PROFILE = "FROM_PROFILE"
        const val ROUTE = "choose_category?from={from}"
        fun createRoute(from: String) = "choose_category?from=$from"
    }

    // ========= Bottom navigation =========
    object Landing : AppRoute("landing", "Accueil", Icons.Filled.Home)
    object Search : AppRoute("search", "Recherche", Icons.Filled.Search)
    object Cart : AppRoute("cart", "Panier", Icons.Filled.ShoppingCart)
    object Favorite : AppRoute("favorite", "Favoris", Icons.Filled.FavoriteBorder)
    object Profile : AppRoute("profile", "Profil", Icons.Filled.Person)

    // ========= Détails =========
    sealed class RestaurantDetail(restaurantId: Int) :
        AppRoute("restaurant_detail/$restaurantId", "Restaurant Détail") {
        companion object {
            const val ROUTE = "restaurant_detail/{restaurantId}"
            fun createRoute(restaurantId: Int) = "restaurant_detail/$restaurantId"
        }
    }

    sealed class RecipeDetail(mealId: Int) :
        AppRoute("recipe_detail/$mealId", "Recette Détail") {
        companion object {
            const val ROUTE = "recipe_detail/{mealId}"
            fun createRoute(mealId: String) = "recipe_detail/$mealId"
        }
    }
}
