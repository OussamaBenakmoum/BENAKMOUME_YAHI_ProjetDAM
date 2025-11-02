package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.viewmodel.ChooseCategoryViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChooseCategoryScreen(
    navController: NavHostController,
    viewModel: ChooseCategoryViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val mainColor = Color(0xFFFF6E41)
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Choisissez votre catégorie", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text("3 sur 5", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(Modifier.height(40.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = mainColor)
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
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        uiState.categories.forEach { label ->
                            val isSelected = uiState.selectedCategories.contains(label)

                            Surface(
                                onClick = { viewModel.toggleCategory(label) },
                                shape = CircleShape,
                                color = if (isSelected) Color(0xFFFFE7DE) else Color(0xFFF1F2F4),
                                tonalElevation = 0.dp,
                                shadowElevation = 0.dp,
                                border = null
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) mainColor else Color.Black,
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            Button(
                onClick = {
                    // TODO: persister uiState.selectedCategories si nécessaire
                    navController.navigate(AppRoute.Landing.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                enabled = !uiState.isLoading
            ) {
                Text("Continuer", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
