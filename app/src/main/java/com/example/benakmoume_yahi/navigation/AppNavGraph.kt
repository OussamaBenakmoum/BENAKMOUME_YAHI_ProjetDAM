package com.example.benakmoume_yahi.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean = false
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) AppRoute.Landing.route else AppRoute.Welcome.route,
        modifier = modifier.fillMaxSize()//.background(Color.Green).padding(40.dp)
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
    Scaffold(
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
            composable(AppRoute.Profile.route) { ProfileScreen(bottomNavController) }

            // Detail screen (Bottom bar hidden)
            composable(AppRoute.RecipeDetail.route) { RecipeDetailScreen(bottomNavController) }
        }
    }
}
/*
@Composable
fun MainBottomNavGraph() {
    val bottomNavController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(bottomNavController)}
    )
    { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = AppRoute.Landing.route,

            Modifier.padding(innerPadding)
        ) {
            composable(AppRoute.Landing.route) { LandingScreen(bottomNavController) }
            composable(AppRoute.Search.route) { SearchScreen(bottomNavController) }
            composable(AppRoute.Cart.route) { CartScreen(bottomNavController) }
            composable(AppRoute.Profile.route) { ProfileScreen(bottomNavController) }

            // Detail screens can be navigated to from Landing/Search
            //composable(AppRoute.RestaurantDetail.route) { RestaurantDetailScreen(bottomNavController) }
            composable(AppRoute.RecipeDetail.route) { RecipeDetailScreen(bottomNavController) }
        }
    }
}

*/
@Composable
fun ThinNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = containerColor,
        tonalElevation = tonalElevation,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
            content = content
        )
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

    NavigationBar {
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
                        imageVector = screen.icon ?: Icons.Default.Home,
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
/*fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        AppRoute.Landing,
        AppRoute.Search,
        AppRoute.Cart,
        AppRoute.Profile
    )
    ThinNavigationBar() {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { screen ->
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { /* navigation logic here */ }
            ) {
                screen.icon?.let {
                    androidx.compose.material3.Icon(
                        it,
                        contentDescription = screen.title
                    )
                }
            }
        }
    }
}
*/


@Preview(showBackground = true)
@Composable
fun AppNavGraphPreview() {
    val navController = rememberNavController()
    AppNavGraph(navController = navController, isLoggedIn = true)
}

@Preview(showBackground = true)
@Composable
fun MainBottomNavGraphPreview() {
    MainBottomNavGraph()
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    val navController = rememberNavController()
    BottomNavBar(navController = navController)
}