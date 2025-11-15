package com.example.benakmoume_yahi.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.benakmoume_yahi.Auth.AuthRepository
import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.components.OSMRestaurantMap
import com.example.benakmoume_yahi.components.RecipeCard
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.viewmodel.ChooseCategoryViewModel
import com.example.benakmoume_yahi.viewmodel.SearchViewModel

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    val navController = rememberNavController()
    LandingScreen(navController = navController)
}

@Composable
fun LandingScreen(
    navController: NavHostController,
    viewModel: ChooseCategoryViewModel = viewModel(),
    viewModelAiRecipe: SearchViewModel = viewModel()
) {
    val mainColor = Color(0xFFFF6E41)

    // Utilisateur connecté
    val authRepo = AuthRepository()
    val currentUser by authRepo.currentUserFlow.collectAsState()

    // State catégories
    val uiState = viewModel.uiState

    // State recettes/restaurants
    val uiStateAiRecipe by viewModelAiRecipe.uiState.collectAsState()

    // Démo: recherche simple
    viewModelAiRecipe.onSearchQueryChanged("e")

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Bonjour ", fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
                Text(currentUser?.displayName ?: "Invité", fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))

            // Recommandées pour toi
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                border = BorderStroke(0.5.dp, color = mainColor)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recommandées pour toi",
                        style = TextStyle(
                            brush = Brush.linearGradient(colors = listOf(Color.Black, Color(0xFF7A5AF8))),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Image(
                        painter = painterResource(R.drawable.aistars),
                        contentDescription = "AI",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Puces catégories
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Color.Black) }
                    }
                    uiState.error != null -> {
                        Text(
                            text = "Erreur: ${uiState.error}",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    else -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(vertical = 6.dp).padding(horizontal = 10.dp)
                        ) {
                            items(uiState.categories.size) { index ->
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFFE7DE),
                                    tonalElevation = 0.dp,
                                    shadowElevation = 0.dp,
                                    border = null
                                ) {
                                    Text(
                                        text = uiState.categories[index],
                                        color = mainColor,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Recettes AI
                when {
                    uiStateAiRecipe.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Color.Black) }
                    }
                    uiStateAiRecipe.error != null -> {
                        Text(
                            text = "Erreur: ${uiStateAiRecipe.error}",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    else -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            items(uiStateAiRecipe.recipes.size) { index ->
                                val recipe = uiStateAiRecipe.recipes[index]
                                RecipeCard(
                                    recipe = recipe,
                                    true,
                                    onClick = {
                                        navController.navigate(
                                            AppRoute.RecipeDetail.createRoute(recipe.id_meal)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }


            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Restaurants autour de toi ",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                //Spacer(Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Carte",
                    tint = mainColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Carte OSM (sans bandeau)
            Card(
                modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                border = BorderStroke(0.5.dp, color = mainColor)
            ) {
                when {
                    uiStateAiRecipe.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Color.Black) }
                    }
                    uiStateAiRecipe.error != null -> {
                        Text(
                            text = "Erreur: ${uiStateAiRecipe.error}",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    else -> {
                        val displayedRestaurants = uiStateAiRecipe.restaurants
                        Box(modifier = Modifier.fillMaxSize()) {
                            OSMRestaurantMap(
                                latitude = 45.719638,
                                longitude = 4.918317,
                                restaurantName = "Vous",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clipToBounds(),
                                displayedRestaurants
                            )
                        }
                    }
                }
            }
        }
    }
}
