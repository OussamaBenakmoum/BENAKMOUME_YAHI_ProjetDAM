package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.viewmodel.ChooseCuisineViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChooseCuisineScreen(
    navController: NavHostController,
    viewModel: ChooseCuisineViewModel = viewModel(),
    modifier: Modifier = Modifier,
    from: String = AppRoute.ChooseCategory.FROM_SIGNUP
) {
    val mainColor = Color(0xFFFF6E41)
    val uiState = viewModel.uiState
    val cuisines = uiState.areas
    val isFromProfile = from == AppRoute.ChooseCategory.FROM_PROFILE

    val scope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

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
                text = if (isFromProfile) "Préférences" else "2 sur 5",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    uiState.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = mainColor)
                        }
                    }
                    uiState.error != null -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = uiState.error ?: "Erreur", color = Color.Red)
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                cuisines.forEach { label ->
                                    val isSelected = uiState.selectedAreas.contains(label)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.toggleCuisine(label) },
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
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            fun saveAndNavigate(onDone: () -> Unit) {
                scope.launch {
                    saving = true
                    error = null
                    try {
                        viewModel.persistSelection(
                            onDone = { onDone() },
                            onError = { msg -> error = msg }
                        )
                    } catch (e: Exception) {
                        error = e.message ?: "Erreur DataStore"
                    } finally {
                        saving = false
                    }
                }
            }

            val enabled = uiState.selectedAreas.isNotEmpty() && !uiState.isLoading && uiState.error == null && !saving

            if (!isFromProfile) {
                Button(
                    onClick = {
                        // Passage de données entre écrans si utile
                        navController.currentBackStackEntry?.savedStateHandle
                            ?.set("selected_cuisines", uiState.selectedAreas.toList())

                        saveAndNavigate {
                            navController.navigate(
                                AppRoute.ChooseCategory.createRoute(AppRoute.ChooseCategory.FROM_SIGNUP)
                            ) { launchSingleTop = true }
                        }
                    },
                    enabled = enabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mainColor)
                ) {
                    if (saving) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Continuer", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                Button(
                    onClick = { saveAndNavigate { navController.popBackStack() } },
                    enabled = enabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mainColor)
                ) {
                    if (saving) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Terminer", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = Color.Red)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
