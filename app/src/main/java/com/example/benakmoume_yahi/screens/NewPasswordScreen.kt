package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.navigation.AppRoute
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreen(
    navController: NavHostController,
    oobCode: String, // code extrait du lien
    modifier: Modifier = Modifier
) {
    val mainColor = Color(0xFFFF6E41)
    val fieldShape = RoundedCornerShape(22.dp)
    var pwd by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }

    // Vérifier le code au montage pour éviter de saisir si invalide/expiré
    LaunchedEffect(oobCode) {
        try {
            loading = true
            auth.verifyPasswordResetCode(oobCode).await() // renvoie l'email si besoin
            loading = false
        } catch (e: Exception) {
            loading = false
            error = e.message ?: "Lien invalide ou expiré"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nouveau mot de passe", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Saisissez votre nouveau mot de passe et confirmez‑le.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(22.dp))

            OutlinedTextField(
                value = pwd,
                onValueChange = { pwd = it },
                placeholder = { Text("Nouveau mot de passe") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = fieldShape,
                isError = pwd.isNotEmpty() && pwd.length < 8,
                supportingText = {
                    if (pwd.isNotEmpty() && pwd.length < 8) {
                        Text(
                            "Au moins 8 caractères requis",
                            color = mainColor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F2F4),
                    unfocusedContainerColor = Color(0xFFF1F2F4),
                    errorContainerColor = Color(0xFFF1F2F4),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    errorBorderColor = mainColor
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                placeholder = { Text("Confirmer le mot de passe") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = fieldShape,
                isError = confirm.isNotEmpty() && confirm != pwd,
                supportingText = {
                    if (confirm.isNotEmpty() && confirm != pwd) {
                        Text(
                            "Les mots de passe ne correspondent pas",
                            color = mainColor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F2F4),
                    unfocusedContainerColor = Color(0xFFF1F2F4),
                    errorContainerColor = Color(0xFFF1F2F4),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    errorBorderColor = mainColor
                )
            )

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {
                    if (pwd.length < 8 || pwd != confirm || loading) return@Button
                    scope.launch {
                        loading = true
                        try {
                            auth.confirmPasswordReset(oobCode, pwd).await()
                            snackbar.showSnackbar("Mot de passe mis à jour")
                            navController.navigate(AppRoute.SignIn.route) {
                                popUpTo(AppRoute.Welcome.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        } catch (e: Exception) {
                            snackbar.showSnackbar(e.message ?: "Échec de la mise à jour")
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading && pwd.length >= 8 && pwd == confirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mainColor,
                    contentColor = Color.White,
                    disabledContainerColor = mainColor.copy(alpha = 0.6f),
                    disabledContentColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        "Continuer",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

