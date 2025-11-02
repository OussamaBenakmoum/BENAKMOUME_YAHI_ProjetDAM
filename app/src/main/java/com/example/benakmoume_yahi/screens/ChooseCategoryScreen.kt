package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.navigation.AppRoute


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChooseCategoryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val mainColor = Color(0xFFFF6E41)

    val categories = listOf(
        "Fromage", "Pâtes", "Burger", "Miel", "Spaghetti",
        "Steak", "Vegan", "Sushi", "Nouilles", "Pizza", "Pain"
    )

    var selected by remember { mutableStateOf(setOf<String>()) }

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

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                categories.forEach { label ->
                    val isSelected = selected.contains(label)

                    // Puce ronde “grande” sans bordure, fond changeant
                    Surface(
                        onClick = {
                            selected = if (isSelected) selected - label else selected + label
                        },
                        shape = CircleShape,
                        color = if (isSelected) Color(0xFFFFE7DE) else Color(0xFFF1F2F4),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        border = null // aucune bordure
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) mainColor else Color.Black,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 14.dp) // hauteur visuelle ↑
                        )
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            Button(
                onClick = {
                    // TODO: persister selected si nécessaire
                    navController.navigate(AppRoute.Landing.route) {
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
