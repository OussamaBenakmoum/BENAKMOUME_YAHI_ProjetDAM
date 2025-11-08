package com.example.benakmoume_yahi.navigation

import android.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

// Screens
import com.example.benakmoume_yahi.screens.CartScreen
import com.example.benakmoume_yahi.screens.ChooseCategoryScreen
import com.example.benakmoume_yahi.screens.ChooseCuisineScreen
import com.example.benakmoume_yahi.screens.ForgotPasswordScreen
import com.example.benakmoume_yahi.screens.LandingScreen
import com.example.benakmoume_yahi.screens.LoginOrSignUpScreen
import com.example.benakmoume_yahi.screens.ProfileScreen
import com.example.benakmoume_yahi.screens.RecipeDetailScreen
import com.example.benakmoume_yahi.screens.RestaurantDetailScreen
import com.example.benakmoume_yahi.screens.SearchScreen
import com.example.benakmoume_yahi.screens.SignInScreen
import com.example.benakmoume_yahi.screens.SignUpScreen
import com.example.benakmoume_yahi.screens.WelcomeScreen
import com.example.benakmoume_yahi.screens.GoogleSignUpScreen
import com.example.benakmoume_yahi.screens.NewPasswordScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean = false
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) AppRoute.Landing.route else AppRoute.Welcome.route,
        modifier = modifier.fillMaxSize()
    ) {
        // Auth flow
        composable(AppRoute.Welcome.route) { WelcomeScreen(navController) }
        composable(AppRoute.LoginOrSignUp.route) { LoginOrSignUpScreen(navController) }
        composable(AppRoute.SignUp.route) { SignUpScreen(navController) }
        composable(AppRoute.SignIn.route) { SignInScreen(navController) }
        composable(AppRoute.ForgotPassword.route) { ForgotPasswordScreen(navController) }

        // Deep link reset password → NewPasswordScreen
        composable(
            route = "new_password?oobCode={oobCode}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://gogourmet-1e044.web.app/reset-return?oobCode={oobCode}"
                },
                navDeepLink {
                    uriPattern = "https://gogourmet-1e044.firebaseapp.com/reset-return?oobCode={oobCode}"
                }
            ),
            arguments = listOf(
                navArgument("oobCode") { type = NavType.StringType; defaultValue = "" }
            )
        ) { entry ->
            val code = entry.arguments?.getString("oobCode").orEmpty()
            NewPasswordScreen(navController = navController, oobCode = code)
        }

        // Compléter profil après Google
        composable(AppRoute.GoogleSignUp.route) { GoogleSignUpScreen(nav = navController) }

        // Option A: accessibles aussi depuis l’auth
        composable(AppRoute.ChooseCuisine.route) { ChooseCuisineScreen(navController) }
        composable(AppRoute.ChooseCategory.route) { ChooseCategoryScreen(navController) }

        // Graphe principal avec bottom bar
        composable(AppRoute.Landing.route) {
            MainBottomNavGraph(rootNav = navController)
        }
    }
}

@Composable
fun MainBottomNavGraph(rootNav: NavHostController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        AppRoute.Landing.route,
        AppRoute.Search.route,
        AppRoute.Cart.route,
        AppRoute.Profile.route
    )

    Scaffold(
        contentWindowInsets = WindowInsets(top = 0.dp),
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavBar(bottomNavController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = AppRoute.Landing.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoute.Landing.route) { LandingScreen(bottomNavController) }
            composable(AppRoute.Search.route) { SearchScreen(bottomNavController) }
            composable(AppRoute.Cart.route) { CartScreen(bottomNavController) }

            composable(AppRoute.Profile.route) {
                ProfileScreen(
                    nav = bottomNavController,
                    onFavoritesCuisines = { bottomNavController.navigate(AppRoute.ChooseCuisine.route) },
                    onFavoritesCategories = { bottomNavController.navigate(AppRoute.ChooseCategory.route) },
                    onSettings = { /* disabled */ },
                    onAbout = { /* disabled */ },
                    onSignOut = {
                        rootNav.navigate(AppRoute.SignIn.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Option A
            composable(AppRoute.ChooseCuisine.route) { ChooseCuisineScreen(bottomNavController) }
            composable(AppRoute.ChooseCategory.route) { ChooseCategoryScreen(bottomNavController) }

            // Détails
            composable(
                route = AppRoute.RecipeDetail.ROUTE,
                arguments = listOf(navArgument("mealId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mealId = backStackEntry.arguments?.getString("mealId") ?: return@composable
                RecipeDetailScreen(navController = bottomNavController, mealId = mealId)
            }
            composable(
                route = AppRoute.RestaurantDetail.ROUTE,
                arguments = listOf(navArgument("restaurantId") { type = NavType.IntType })
            ) { backStackEntry ->
                val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: return@composable
                RestaurantDetailScreen(navController = bottomNavController, restaurantId = restaurantId)
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        AppRoute.Landing,
        AppRoute.Search,
        AppRoute.Cart,
        AppRoute.Profile
    )
    NavigationBar(windowInsets = WindowInsets(bottom = 0.dp, top = 0.dp)) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon ?: ImageVector.vectorResource(R.drawable.ic_menu_gallery),
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
