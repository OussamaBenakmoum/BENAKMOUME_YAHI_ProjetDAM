package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.Auth.AuthViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: AuthViewModel = viewModel()
) {
    val mainColor = Color(0xFFFF6E41)
    val fieldShape = RoundedCornerShape(22.dp)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Etat d'auth (loading, user, error)
    val ui by vm.state.collectAsState()

    // Navigation après succès
    LaunchedEffect(ui.user) {
        if (ui.user != null) {
            navController.navigate(AppRoute.Landing.route) {
                popUpTo(AppRoute.Welcome.route) { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Connexion",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.heightIn(min = 70.dp, max = 80.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            // Image header
            Image(
                painter = painterResource(id = R.drawable.signinimage),
                contentDescription = "Header",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            // Contenu scrollable
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Let's Cook Again",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(18.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = fieldShape,
                    trailingIcon = {
                        if (email.contains("@") && email.contains(".")) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Valid",
                                tint = Color(0xFF27AE60)
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF1F2F4),
                        unfocusedContainerColor = Color(0xFFF1F2F4),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(Modifier.height(12.dp))

                // Mot de passe avec toggle visibilité
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Mot de passe") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = fieldShape,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val desc = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = desc)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF1F2F4),
                        unfocusedContainerColor = Color(0xFFF1F2F4),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(Modifier.height(18.dp))

                // Connexion
                Button(
                    onClick = { vm.signIn(email.trim(), password) },
                    enabled = email.isNotBlank() && password.isNotBlank() && !ui.loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mainColor,
                        contentColor = Color.White,
                        disabledContainerColor = mainColor.copy(alpha = 0.6f),
                        disabledContentColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    if (ui.loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Se connecter", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                }


                // Erreur backend éventuelle
                if (ui.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        ui.error ?: "",
                        color = mainColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = { navController.navigate(AppRoute.ForgotPassword.route) }) {
                    Text("Mot de passe oublié ?", color = mainColor, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(Modifier.height(4.dp))

                Row {
                    Text("Vous n'avez pas un compte? ", color = Color.Gray)
                    Text(
                        "S'inscrire",
                        color = mainColor,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable { navController.navigate(AppRoute.SignUp.route) }
                    )
                }

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
