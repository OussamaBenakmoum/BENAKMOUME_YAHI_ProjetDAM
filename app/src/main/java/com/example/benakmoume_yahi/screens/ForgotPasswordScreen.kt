package com.example.benakmoume_yahi.screens

import android.util.Patterns
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val mainColor = Color(0xFFFF6E41)
    val fieldShape = RoundedCornerShape(22.dp)
    var email by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    var sending by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }

    // Option: forcer l’ouverture dans l’app via Hosting + deep link
    val actionCodeSettings = remember {
        ActionCodeSettings.newBuilder()
            .setUrl("https://gogourmet-1e044.web.app/reset-return")
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                "com.example.benakmoume_yahi",
                true,   // installIfNotAvailable
                "1"     // min version
            )
            .build()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mot de passe oublié", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                text = "Saisissez l’adresse e‑mail associée à votre compte et nous vous enverrons un lien pour réinitialiser votre mot de passe.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(22.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it.trim()
                    isEmailError = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                },
                placeholder = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = fieldShape,
                isError = isEmailError,
                supportingText = {
                    if (isEmailError) {
                        Text(
                            "Format d’e‑mail invalide. Exemple : email@email.com",
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
                    val valid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    isEmailError = !valid
                    if (!valid || sending) return@Button

                    scope.launch {
                        sending = true
                        try {
                            // Variante 1: simple
                            // auth.sendPasswordResetEmail(email).await()

                            // Variante 2 (recommandée): via actionCodeSettings pour ouvrir l’app
                            auth.sendPasswordResetEmail(email, actionCodeSettings).await()

                            snackbarHostState.showSnackbar("Email de réinitialisation envoyé")
                            navController.navigateUp()
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(e.message ?: "Erreur lors de l’envoi")
                        } finally {
                            sending = false
                        }
                    }
                },
                enabled = !sending && !isEmailError && email.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mainColor,
                    contentColor = Color.White,
                    disabledContainerColor = mainColor.copy(alpha = 0.6f), // même teinte quand disabled
                    disabledContentColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                if (sending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Continuer", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
