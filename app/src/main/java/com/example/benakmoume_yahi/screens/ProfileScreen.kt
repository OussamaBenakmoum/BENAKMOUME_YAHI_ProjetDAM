package com.example.benakmoume_yahi.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.Auth.AuthViewModel
import androidx.preference.PreferenceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavHostController,
    vm: AuthViewModel = viewModel(),
    onFavoritesCuisines: () -> Unit = { nav.navigate(AppRoute.ChooseCuisine.route) },
    onFavoritesCategories: () -> Unit = {
        nav.navigate(AppRoute.ChooseCategory.createRoute(AppRoute.ChooseCategory.FROM_PROFILE))
    },
    onSettings: () -> Unit = {},
    onAbout: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val userState by vm.state.collectAsState()
    val context = LocalContext.current
    val prefs = remember { PreferenceManager.getDefaultSharedPreferences(context) }
    val mainColor = Color(0xFFFF6E41)
    val listBg = Color(0xFFF4F2EE) // Fond uniquement pour la liste

    // Photo de profil locale persistée
    var localPhotoUri by rememberSaveable { mutableStateOf(prefs.getString("profile_photo_uri", null)) }

    // Pick image
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            localPhotoUri = uri.toString()
            prefs.edit().putString("profile_photo_uri", localPhotoUri).apply()
        }
    }

    // Permissions
    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pickImage.launch("image/*")
    }

    fun askPhotoPermissionOrPick() {
        val permission = if (Build.VERSION.SDK_INT >= 33)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        val granted = ContextCompat.checkSelfPermission(
            context, permission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (granted) pickImage.launch("image/*") else requestPermission.launch(permission)
    }

    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
            // Pas de background global ici: on laisse la surface par défaut du thème
        ) {
            // Titre
            Text(
                text = "Profil",
                color = Color.Black,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                val displayName = userState.user?.displayName.orEmpty()
                val email = userState.user?.email.orEmpty()

                val initials = buildString {
                    displayName.split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .forEach { append(it.first().uppercaseChar()) }
                }.ifBlank { "GG" }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .clickable { askPhotoPermissionOrPick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (localPhotoUri.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color(0xFFFFE6DF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = mainColor
                                )
                            }
                        } else {
                            AsyncImage(
                                model = localPhotoUri,
                                contentDescription = "Photo de profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                    }

                    // Badge édition
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { askPhotoPermissionOrPick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Modifier",
                            tint = mainColor
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(displayName.ifBlank { "Invité" }, style = MaterialTheme.typography.titleMedium)
                if (email.isNotBlank()) {
                    Text(email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Spacer(Modifier.height(24.dp))

                // Liste d’options: fond appliqué UNIQUEMENT ici
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF4F2EE), shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        ProfileItem(title = "Mes cuisines préférées") { onFavoritesCuisines() }
                        Divider()
                        ProfileItem(title = "Mes catégories préférées") { onFavoritesCategories() }
                        Divider()
                        ProfileItemDisabled(title = "Paramètres")
                        Divider()
                        ProfileItemDisabled(title = "À propos de GoGourmet")
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Se déconnecter",
                    style = MaterialTheme.typography.titleMedium,
                    color = mainColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            vm.signOut()
                            onSignOut()
                        }
                        .padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
private fun ProfileItemDisabled(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}
