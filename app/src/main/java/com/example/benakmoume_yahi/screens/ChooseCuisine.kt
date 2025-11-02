package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.navigation.AppRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseCuisineScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val mainColor = Color(0xFFFF6E41)

    // Liste d’options (tu peux la charger d’un repo plus tard)
    val cuisines = listOf(
        "Chinoise", "Indonésienne", "Brésilienne", "Japonaise", "Italienne",
        "Américaine", "Marocaine", "Indienne", "Mexicaine", "Thaï", "Méditerranéenne"
    )

    // Etat de sélection multiple
    var selected by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Choisissez votre cuisine", style = MaterialTheme.typography.titleLarge) },
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

            Text(
                text = "3 sur 5",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            // Les chips sur plusieurs lignes
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cuisines.forEach { label ->
                    val isSelected = selected.contains(label)

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selected = if (isSelected) {
                                selected - label           // retire si déjà sélectionné
                            } else {
                                selected + label           // ajoute sinon
                            }
                        },
                        label = {
                            Text(
                                label,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                                color = if (isSelected) mainColor else Color.Black
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFFF1F2F4),
                            selectedContainerColor = Color(0xFFFFE7DE),
                            labelColor = Color.Black,
                            selectedLabelColor = mainColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            disabledSelectedBorderColor = Color.Transparent,
                            borderWidth = 0.dp,
                            selectedBorderWidth = 0.dp
                        )
                    )
                }
            }


            Spacer(Modifier.height(50.dp))

            Button(
                onClick = {
                    // TODO: persister la sélection, puis aller à la suite (ex: Landing)
                    navController.navigate(AppRoute.ChooseCategory.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mainColor)
            ) {
                Text("Continuer", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
