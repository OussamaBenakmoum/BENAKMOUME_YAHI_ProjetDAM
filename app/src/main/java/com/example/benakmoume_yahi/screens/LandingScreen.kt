package com.example.benakmoume_yahi.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
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
import com.example.benakmoume_yahi.components.RestaurantCard
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.viewmodel.ChooseCategoryViewModel
import com.example.benakmoume_yahi.viewmodel.SearchViewModel


@Preview(showBackground = true)
@Composable
fun LandingScreenPreview()
{
    val navController = rememberNavController()
    LandingScreen(navController = navController)
}

@Composable
fun LandingScreen(navController: NavHostController, viewModel: ChooseCategoryViewModel = viewModel(), viewModelAiRecipe: SearchViewModel = viewModel())
{
    //val authRepo = AuthRepository.getInstance()
    //val currentUser by authRepo.currentUserFlow.collectAsState()
    val uiState = viewModel.uiState

    val uiStateAiRecipe by viewModelAiRecipe.uiState.collectAsState()


    viewModelAiRecipe.onSearchQueryChanged("e")

    Column(modifier = Modifier.fillMaxSize().padding(10.dp))
    {
        Column (modifier = Modifier.fillMaxSize())
        {
            Row (modifier = Modifier.fillMaxWidth())
            {
                Text("Bonjour ", fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
                Text("Oussama", fontSize = 26.sp, fontWeight = FontWeight.SemiBold)//Text(currentUser?.uid ?: "Non connecté")
            }

            Card(//#7A5AF8
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                border = BorderStroke(0.5.dp, Color(0xFF7A5AF8))
            )
            {
                Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp))
                {
                    Text(text = "Recommandées pour toi", style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Black,
                                Color(0xFF7A5AF8)
                            )
                        ),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    )

                    Image(
                        painter = painterResource(R.drawable.aistars),
                        contentDescription = "test",
                        modifier = Modifier.size(24.dp)
                    )
                }

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.Black)
                        }
                    }

                    uiState.error != null -> {
                        Text(
                            text = "Erreur: ${uiState.error}",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(vertical = 6.dp).padding(horizontal = 10.dp)
                        )
                        {
                            items(uiState.categories.size) { index ->
                                Surface(
                                    //onClick = { Log.d("test","ddd") },
                                    shape = CircleShape,
                                    color = Color(0xFFF1F2F4),

                                    tonalElevation = 0.dp,
                                    shadowElevation = 0.dp,
                                    border = BorderStroke(1.dp, Color(0xFFA90B3D))
                                ) {
                                    Text(
                                        text = uiState.categories[index],
                                        color = Color(0xFFA90B3D),
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(
                                            horizontal = 18.dp,
                                            vertical = 12.dp
                                        )
                                    )
                                }
                            }
                        }


                    }
                }

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
            Card(
                modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                border = BorderStroke(0.5.dp, Color(0xFF7A5AF8))
            )
            {
                Log.d("benakout", uiStateAiRecipe.restaurants.size.toString())
                when {
                    uiStateAiRecipe.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.Black)
                        }
                    }

                    uiStateAiRecipe.error != null -> {
                        Text(
                            text = "Erreur: ${uiState.error}",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> {
                        val displayedRestaurants = uiStateAiRecipe.restaurants

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


        //Button(onClick = {navController.navigate(AppRoute.RestaurantDetail.Companion.createRoute(1))}) { }
    }


}

