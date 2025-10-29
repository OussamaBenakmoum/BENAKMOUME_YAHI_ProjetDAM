package com.example.benakmoume_yahi.navigation

import android.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.benakmoume_yahi.screens.CartScreen
import com.example.benakmoume_yahi.screens.LandingScreen
import com.example.benakmoume_yahi.screens.LoginOrSignUpScreen
import com.example.benakmoume_yahi.screens.ProfileScreen
import com.example.benakmoume_yahi.screens.RecipeDetailScreen
import com.example.benakmoume_yahi.screens.SearchScreen
import com.example.benakmoume_yahi.screens.WelcomeScreen
import com.example.benakmoume_yahi.utils.restaurantList

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean = true
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) AppRoute.Landing.route else AppRoute.Welcome.route,
        modifier = modifier.fillMaxSize()/*background(Color.Green).padding(10.dp)*/
    ) {
        // Authentication flow
        composable(AppRoute.Welcome.route) { WelcomeScreen(navController) }
        composable(AppRoute.LoginOrSignUp.route) { LoginOrSignUpScreen(navController) }

        // Landing page with nested bottom navigation graph
        composable(AppRoute.Landing.route) {
            //Column (modifier = Modifier.background(Color.Red).fillMaxSize())

                MainBottomNavGraph()



        }
    }
}

@Composable
fun MainBottomNavGraph() {
    val bottomNavController = rememberNavController()

    // Get current route
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define routes where the BottomNavBar should be visible
    val bottomBarRoutes = listOf(
        AppRoute.Landing.route,
        AppRoute.Search.route,
        AppRoute.Cart.route,
        AppRoute.Profile.route
    )

    // Conditionally show BottomNavBar only for selected routes
    Scaffold( contentWindowInsets = WindowInsets(top= 0.dp),
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavBar(bottomNavController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = AppRoute.Search/*Landing*/.route,
            modifier = Modifier.padding(innerPadding)/*.background(Color.Cyan).padding(10.dp)*/
        ) {
            composable(AppRoute.Landing.route) { LandingScreen(bottomNavController) }
            composable(AppRoute.Search.route) { SearchScreen(bottomNavController) }
            composable(AppRoute.Cart.route) { CartScreen(bottomNavController, restaurant = restaurantList[0]) }
            composable(AppRoute.Profile.route) { ProfileScreen(bottomNavController) }

            // Detail screen (Bottom bar hidden)
            composable(AppRoute.RecipeDetail.route) { RecipeDetailScreen(bottomNavController) }

            composable(AppRoute.RestaurantDetail.route) { RecipeDetailScreen(bottomNavController) }

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
    NavigationBar (windowInsets = WindowInsets(bottom = 0.dp, top = 0.dp)){
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
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
                        imageVector = screen.icon ?: ImageVector.vectorResource(R.drawable.gallery_thumb),
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

@Preview(showBackground = true)
@Composable
fun AppNavGraphPreview() {
    val navController = rememberNavController()
    AppNavGraph(navController = navController, isLoggedIn = true)
}