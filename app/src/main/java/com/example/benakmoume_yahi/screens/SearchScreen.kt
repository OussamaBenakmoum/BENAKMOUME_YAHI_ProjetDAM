package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.benakmoume_yahi.components.RecipeCard
import com.example.benakmoume_yahi.components.RestaurantCard
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.viewmodel.SearchViewModel

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen() {
    val navController = rememberNavController()
    // SearchScreen(navController)
}

@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isFocused by remember { mutableStateOf(false) }
    var showAllRestaurants by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Search",
            color = Color.Black,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
        )

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            shape = RoundedCornerShape(24.dp),
            placeholder = { Text("Trouver une recette/ un restaurant...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 23.dp, vertical = 10.dp)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF4F2EE),
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.Red,
                unfocusedLabelColor = Color.LightGray,
            )
        )

        // Affichage du loading
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF4F2EE)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }
        // Affichage de l'erreur
        else if (uiState.error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF4F2EE))
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "❌ Erreur",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error ?: "Une erreur est survenue",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
        // Message d'information si la recherche est vide
        else if (uiState.searchQuery.isEmpty() || uiState.searchQuery.isBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF4F2EE))
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Aucun résultat",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tapez dans la barre de recherche\npour trouver des recettes et restaurants",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
        // Affichage des résultats
        else {
            // Message si aucun résultat trouvé
            if (uiState.recipes.isEmpty() && uiState.restaurants.isEmpty())
            {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF4F2EE))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aucun résultat trouvé",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Essayez avec d'autres mots-clés",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else
            {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Section Recettes - affichée seulement si des recettes sont trouvées
                    if (uiState.recipes.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .background(Color(0xFFF4F2EE))
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Recettes (${uiState.recipes.size})",
                                    color = Color.Black,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                /*Text(
                                    text = "Plus +",
                                    color = Color.Black,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .clickable { /* Navigation vers toutes les recettes */ }
                                )*/
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                items(uiState.recipes.size) { index ->
                                    val recipe = uiState.recipes[index]
                                    RecipeCard(
                                        recipe = recipe,
                                        onClick = {
                                            navController.navigate(
                                                AppRoute.RecipeDetail.createRoute(recipe.id_meal)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .background(Color.White)
                                .height(12.dp)
                        )
                    }

                    // Section Restaurants - affichée seulement si des restaurants sont trouvés
                    if (uiState.restaurants.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .background(Color(0xFFF4F2EE))
                                .padding(horizontal = 8.dp)
                                .fillMaxSize()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Restaurants (${uiState.restaurants.size})",
                                    color = Color.Black,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            val displayedRestaurants = if (showAllRestaurants) {
                                uiState.restaurants
                            } else {
                                uiState.restaurants.take(2)
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(displayedRestaurants.size) { index ->
                                    val restaurant = displayedRestaurants[index]
                                    Column {
                                        RestaurantCard(
                                            restaurant = restaurant,
                                            onClick = {
                                                navController.navigate(
                                                    AppRoute.RestaurantDetail.createRoute(restaurant.id)
                                                )
                                            }
                                        )
                                        Spacer(
                                            modifier = Modifier
                                                .background(Color.White)
                                                .height(12.dp)
                                        )
                                        HorizontalDivider(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp),
                                            thickness = DividerDefaults.Thickness,
                                            color = Color(0xFFcfcfcf)
                                        )
                                    }
                                }
                            }

                            if (!showAllRestaurants && uiState.restaurants.size > 2) {
                                Text(
                                    text = "Afficher tous les restaurants (${uiState.restaurants.size})",
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .clickable { showAllRestaurants = true }
                                        .align(Alignment.CenterHorizontally)
                                        .padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



