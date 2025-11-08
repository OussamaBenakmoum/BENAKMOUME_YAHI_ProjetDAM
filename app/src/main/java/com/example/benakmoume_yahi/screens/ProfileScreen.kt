package com.example.benakmoume_yahi.screens

// Compose / runtime
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Layouts & interactions
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

// Material 3
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons as LegacyIcons
import androidx.compose.material.icons.filled.ArrowForwardIos as LegacyArrow

// Navigation
import androidx.navigation.NavHostController

// ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// VM & routes
import com.example.benakmoume_yahi.Auth.AuthViewModel
import com.example.benakmoume_yahi.navigation.AppRoute

// Image (Coil)
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

// Activity results & permissions
import android.Manifest
import android.os.Build
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

// Prefs
import androidx.preference.PreferenceManager

// Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    nav: NavHostController,
    vm: AuthViewModel = viewModel(),
    onFavoritesCuisines: () -> Unit = { nav.navigate(AppRoute.ChooseCuisine.route) },
    onFavoritesCategories: () -> Unit = { nav.navigate(AppRoute.ChooseCategory.route) },
    onSettings: () -> Unit = {},
    onAbout: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val userState by vm.state.collectAsState()
    val context = LocalContext.current
    val prefs = remember { PreferenceManager.getDefaultSharedPreferences(context) }

    // Charger l'URI locale depuis prefs
    var localPhotoUri by rememberSaveable {
        mutableStateOf(prefs.getString("profile_photo_uri", null))
    }

    // Picker d'images
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            localPhotoUri = uri.toString()
            prefs.edit().putString("profile_photo_uri", localPhotoUri).apply()
        }
    }

    // Demande de permission photos
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

        val granted =
            ContextCompat.checkSelfPermission(
                context, permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (granted) pickImage.launch("image/*") else requestPermission.launch(permission)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                title = { Text("Profile", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            val displayName = userState.user?.displayName.orEmpty()
            val email = userState.user?.email.orEmpty()

            // Utiliser l'URI locale comme source principale
            val effectivePhoto = localPhotoUri
            val initials = buildString {
                displayName.split(" ").filter { it.isNotBlank() }.take(2).forEach { append(it.first().uppercaseChar()) }
            }.ifBlank { "GG" }

            // Avatar centré avec padding interne (badge non rogné)
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
                    if (effectivePhoto.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color(0xFFFFE6DF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFFFF6E41)
                            )
                        }
                    } else {
                        AsyncImage(
                            model = effectivePhoto, // content://... depuis le picker
                            contentDescription = "Photo de profil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }

                // Badge crayon
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
                        imageVector = androidx.compose.material.icons.Icons.Filled.Edit,
                        contentDescription = "Modifier",
                        tint = Color(0xFFFF6E41)
                    )
                }
            }

//            // Action optionnelle: supprimer la photo locale
//            TextButton(
//                onClick = {
//                    localPhotoUri = null
//                    prefs.edit().remove("profile_photo_uri").apply()
//                }
//            ) {
//                Text("Supprimer la photo")
//            }

            // Nom / Email
            Spacer(Modifier.height(4.dp))
            Text(displayName.ifBlank { "Invité" }, style = MaterialTheme.typography.titleMedium)
            if (email.isNotBlank()) {
                Text(email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Spacer(Modifier.height(24.dp))

            // Menu
            ProfileItem(title = "Mes cuisines préférées") { onFavoritesCuisines() }
            Divider()
            ProfileItem(title = "Mes catégories préférées") { onFavoritesCategories() }
            Divider()
            ProfileItemDisabled(title = "Paramètres")
            Divider()
            ProfileItemDisabled(title = "About GoGourmet")
            Divider()

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Se déconnecter",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFF6E41),
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
            imageVector = LegacyIcons.Filled.ArrowForwardIos,
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
            imageVector = LegacyIcons.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}
