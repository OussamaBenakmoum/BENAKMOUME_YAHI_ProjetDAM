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
import com.example.benakmoume_yahi.Auth.AuthViewModel
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.remote.RetrofitInstance
import com.example.benakmoume_yahi.viewmodel.ChooseCuisineViewModel
import com.example.benakmoume_yahi.models.UserCreate
import com.example.benakmoume_yahi.models.UserUpdate
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

    val authVm: AuthViewModel = viewModel()
    val uid = authVm.state.collectAsState().value.user?.uid
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
                if (uid.isNullOrBlank()) {
                    onDone()
                    return
                }
                scope.launch {
                    saving = true
                    error = null
                    try {
                        // CSV conforme à l'API (string | null)
                        val cuisinesCsv: String? = uiState.selectedAreas
                            .map { it.toString().trim() }
                            .filter { it.isNotEmpty() }
                            .joinToString(",")
                            .ifBlank { null }

                        // Essayer d'abord une mise à jour (PUT) avec UserUpdate
                        val update = UserUpdate(areas_preferred = cuisinesCsv)
                        val put = RetrofitInstance.api.updateUser(uid, update)
                        if (!put.isSuccessful) {
                            if (put.code() == 404) {
                                // Fallback: création (POST) avec UserCreate complet
                                val user = authVm.state.value.user
                                val first = user?.displayName?.substringBefore(" ")
                                    ?.takeIf { !it.isNullOrBlank() } ?: "User"
                                val last = user?.displayName?.substringAfter(" ", missingDelimiterValue = "")
                                    ?.ifBlank { " " } ?: " "
                                val mail = user?.email ?: "user@example.com"

                                val create = UserCreate(
                                    firstname = first,
                                    lastname = last,
                                    email = mail,
                                    areas_preferred = cuisinesCsv,
                                    preferred_categories = null,
                                    photo_profile = null,
                                    firebase_uid = uid
                                )
                                val post = RetrofitInstance.api.createUser(create)
                                if (!post.isSuccessful) {
                                    error = "Erreur création (${post.code()})"
                                    return@launch
                                }
                            } else {
                                error = "Erreur de sauvegarde (${put.code()})"
                                return@launch
                            }
                        }
                        onDone()
                    } catch (e: Exception) {
                        error = e.message ?: "Erreur réseau"
                    } finally {
                        saving = false
                    }
                }
            }

            if (!isFromProfile) {
                Button(
                    onClick = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("selected_cuisines", uiState.selectedAreas.toList())
                        saveAndNavigate {
                            navController.navigate(
                                AppRoute.ChooseCategory.createRoute(AppRoute.ChooseCategory.FROM_SIGNUP)
                            ) { launchSingleTop = true }
                        }
                    },
                    enabled = uiState.selectedAreas.isNotEmpty() && !uiState.isLoading && uiState.error == null && !saving,
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
                    enabled = uiState.selectedAreas.isNotEmpty() && !uiState.isLoading && uiState.error == null && !saving,
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
