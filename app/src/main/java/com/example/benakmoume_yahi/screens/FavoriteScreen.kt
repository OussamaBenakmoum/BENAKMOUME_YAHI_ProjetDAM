package com.example.benakmoume_yahi.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.benakmoume_yahi.Auth.AuthViewModel
import com.example.benakmoume_yahi.components.FavoriteCard
import com.example.benakmoume_yahi.models.Recipe
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.remote.RetrofitInstance
import com.example.benakmoume_yahi.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(
    navController: NavController,
    favoritesVm: FavoritesViewModel = viewModel(),
    authVm: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val mainColor = Color(0xFFFF6E41)
    val uid = authVm.state.collectAsState().value.user?.uid
    val recipes by favoritesVm.recipes.collectAsState()
    var showAll by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Etat du dialog de confirmation
    var confirmOpen by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        if (!uid.isNullOrBlank()) favoritesVm.loadFavorites(uid)
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Grand titre
        Text(
            text = "Favoris",
            color = Color.Black,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
        )

        // Ligne compteur + TextButton "Tout supprimer"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mes favoris (${recipes.size})",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (recipes.isNotEmpty()) {
                TextButton(
                    onClick = { confirmOpen = true },
                ) {
                    Text("Tout supprimer", color = mainColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Dialog de confirmation
        if (confirmOpen) {
            AlertDialog(
                onDismissRequest = { if (!deleting) confirmOpen = false },
                title = { Text("Supprimer tous les favoris") },
                text = { Text("Cette action va retirer toutes vos recettes des favoris. Continuer ?") },
                confirmButton = {
                    TextButton(
                        enabled = !deleting,
                        onClick = {
                            if (uid.isNullOrBlank()) {
                                confirmOpen = false
                                return@TextButton
                            }
                            deleting = true
                            scope.launch {
                                // 1) Tentative DELETE global
                                val delOk = runCatching {
                                    val del = RetrofitInstance.api.clearFavorites(uid)
                                    del.isSuccessful
                                }.getOrDefault(false)

                                if (delOk) {
                                    // 2) GET de contrôle
                                    val check = runCatching { RetrofitInstance.api.getFavorites(uid) }.getOrNull()
                                    val isNowEmpty = check != null && check.isSuccessful && (check.body().orEmpty()).isEmpty()
                                    if (isNowEmpty) {
                                        favoritesVm.clearFavorites(uid)
                                        Toast.makeText(ctx, "Tous les favoris supprimés", Toast.LENGTH_SHORT).show()
                                    } else {
                                        favoritesVm.loadFavorites(uid)
                                        Toast.makeText(ctx, "Vérification: favoris non vides", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Fallback unitaire (ex: 405)
                                    val listResp = runCatching { RetrofitInstance.api.getFavorites(uid) }.getOrNull()
                                    val items = if (listResp != null && listResp.isSuccessful) listResp.body().orEmpty() else emptyList()
                                    if (items.isEmpty()) {
                                        favoritesVm.clearFavorites(uid)
                                        Toast.makeText(ctx, "Aucun favori à supprimer", Toast.LENGTH_SHORT).show()
                                    } else {
                                        var successCount = 0
                                        items.forEach { fav ->
                                            val ok = runCatching {
                                                RetrofitInstance.api.removeFavorite(uid, fav.id_meal)
                                                true
                                            }.getOrDefault(false)
                                            if (ok) successCount++
                                        }
                                        favoritesVm.loadFavorites(uid)
                                        if (successCount == items.size) {
                                            Toast.makeText(ctx, "Tous les favoris supprimés", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(ctx, "Suppression partielle (${successCount}/${items.size})", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                deleting = false
                                confirmOpen = false
                            }
                        }
                    ) {
                        if (deleting) {
                            CircularProgressIndicator(
                                color = mainColor,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text("Supprimer", color = mainColor, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        enabled = !deleting,
                        onClick = { confirmOpen = false }
                    ) { Text("Annuler") }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (recipes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Aucune recette en favori", color = Color.Gray, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                Text("Ajoutez des recettes depuis la recherche", color = Color.LightGray, fontSize = 14.sp)
            }
            return@Column
        }

        val displayed: List<Recipe> = if (showAll) recipes else recipes.take(2)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFF4F2EE))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayed.size) { index ->
                    val recipe: Recipe = displayed[index]
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Carte cliquable
                            Box(modifier = Modifier.weight(1f)) {
                                FavoriteCard(
                                    recipe = recipe,
                                    onClick = {
                                        if (recipe.id_meal.isNotBlank()) {
                                            navController.navigate(
                                                AppRoute.RecipeDetail.createRoute(recipe.id_meal)
                                            )
                                        }
                                    }
                                )
                            }
                            // Bouton supprimer individuel
                            IconButton(
                                onClick = {
                                    if (!uid.isNullOrBlank()) {
                                        scope.launch {
                                            val ok = runCatching {
                                                RetrofitInstance.api.removeFavorite(uid, recipe.id_meal)
                                                true
                                            }.isSuccess
                                            if (ok) {
                                                favoritesVm.removeLocal(recipe.id_meal)
                                                Toast.makeText(ctx, "Supprimé des favoris", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(ctx, "Échec de suppression", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    tint = mainColor
                                )
                            }
                        }

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
                item { Spacer(Modifier.height(4.dp)) }
            }

            if (!showAll && recipes.size > 2) {
                Text(
                    text = "Afficher toutes les recettes (${recipes.size})",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { showAll = true }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}
