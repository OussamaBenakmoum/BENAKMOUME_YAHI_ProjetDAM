package com.example.benakmoume_yahi.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.benakmoume_yahi.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.benakmoume_yahi.Auth.AuthViewModel
import com.example.benakmoume_yahi.viewmodel.FavoritesViewModel

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
        // ========= Auth flow =========
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

        // Google profile completion
        composable(AppRoute.GoogleSignUp.route) { GoogleSignUpScreen(nav = navController) }

        // Preferences accessibles depuis auth (onboarding)
        composable(AppRoute.ChooseCuisine.route) {
            ChooseCuisineScreen(
                navController = navController,
                from = AppRoute.ChooseCategory.FROM_SIGNUP
            )
        }
        composable(
            route = AppRoute.ChooseCategory.ROUTE,
            arguments = listOf(
                navArgument("from") { type = NavType.StringType; defaultValue = AppRoute.ChooseCategory.FROM_SIGNUP }
            )
        ) { entry ->
            val from = entry.arguments?.getString("from") ?: AppRoute.ChooseCategory.FROM_SIGNUP
            ChooseCategoryScreen(navController = navController, from = from)
        }

        // Main graph avec bottom bar
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
        AppRoute.Favorite.route,
        AppRoute.Profile.route
    )

    Scaffold(
        contentWindowInsets = WindowInsets(top = 0.dp),
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavBar(
                    navController = bottomNavController,
                    mainColor = Color(0xFFFF6E41),
                    barBackground = MaterialTheme.colorScheme.background,)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = AppRoute.Landing.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home / Search
            composable(AppRoute.Landing.route) { LandingScreen(bottomNavController) }
            composable(AppRoute.Search.route) { SearchScreen(bottomNavController) }

            // Favoris réels (VM + Auth)
            composable(AppRoute.Favorite.route) {
                val favoritesVm: FavoritesViewModel = viewModel()
                val authVm: AuthViewModel = viewModel()
                FavoriteScreen(
                    navController = bottomNavController,
                    favoritesVm = favoritesVm,
                    authVm = authVm
                )
            }

            // Profil
            composable(AppRoute.Profile.route) {
                ProfileScreen(
                    nav = bottomNavController,
                    onFavoritesCuisines = {
                        // Accès depuis Profil → ChooseCuisine en mode profile
                        bottomNavController.navigate(AppRoute.ChooseCuisine.route)
                    },
                    onFavoritesCategories = {
                        // Accès depuis Profil → ChooseCategory en mode profile
                        bottomNavController.navigate(
                            AppRoute.ChooseCategory.createRoute(AppRoute.ChooseCategory.FROM_PROFILE)
                        )
                    },
                    onSettings = { /* TODO */ },
                    onAbout = { /* TODO */ },
                    onSignOut = {
                        // Clear stack and go to SignIn
                        rootNav.navigate(AppRoute.SignIn.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Preferences depuis profil
            composable(AppRoute.ChooseCuisine.route) {
                ChooseCuisineScreen(
                    navController = bottomNavController,
                    from = AppRoute.ChooseCategory.FROM_PROFILE
                )
            }
            composable(
                route = AppRoute.ChooseCategory.ROUTE,
                arguments = listOf(
                    navArgument("from") { type = NavType.StringType; defaultValue = AppRoute.ChooseCategory.FROM_PROFILE }
                )
            ) { entry ->
                val from = entry.arguments?.getString("from") ?: AppRoute.ChooseCategory.FROM_PROFILE
                ChooseCategoryScreen(navController = bottomNavController, from = from)
            }

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
fun BottomNavBar(
    navController: NavHostController,
    mainColor: Color = Color(0xFFFF6E41),
    barBackground: Color = MaterialTheme.colorScheme.background
) {
    val items = listOf(
        AppRoute.Landing,
        AppRoute.Search,
        AppRoute.Favorite,
        AppRoute.Profile
    )
    val unselected = Color(0xFF1F1F1F) // noir doux
    val indicator = barBackground // pas de pastille colorée

    NavigationBar(
        windowInsets = WindowInsets(bottom = 0.dp, top = 0.dp),
        containerColor = barBackground,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon ?: Icons.Filled.Favorite,
                        contentDescription = screen.title,
                        tint = if (selected) mainColor else unselected
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        color = if (selected) mainColor else unselected
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = mainColor,
                    selectedTextColor = mainColor,
                    unselectedIconColor = unselected,
                    unselectedTextColor = unselected,
                    indicatorColor = indicator
                )
            )
        }
    }
}