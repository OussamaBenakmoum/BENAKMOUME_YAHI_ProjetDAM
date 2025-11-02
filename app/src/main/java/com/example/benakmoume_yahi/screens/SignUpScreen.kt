package com.example.benakmoume_yahi.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.Auth.AuthViewModel   // adapte le package si différent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: AuthViewModel = viewModel()
) {
    val mainColor = Color(0xFFFF6E41)
    val fieldShape = RoundedCornerShape(22.dp)

    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirm   by remember { mutableStateOf("") }

    var isEmailError    by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var isConfirmError  by remember { mutableStateOf(false) }

    // Etat d'auth
    val ui by vm.state.collectAsState()

    // Navigation après succès
    LaunchedEffect(ui.user) {
        if (ui.user != null) {
            navController.navigate(AppRoute.ChooseCuisine.route) {
                popUpTo(AppRoute.Welcome.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inscription", style = MaterialTheme.typography.titleLarge, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                modifier = Modifier.heightIn(min = 70.dp, max = 80.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.signupimage2),
                contentDescription = "Header",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = { Text("Prénom") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = fieldShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F2F4),
                            unfocusedContainerColor = Color(0xFFF1F2F4),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = { Text("Nom") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = fieldShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F2F4),
                            unfocusedContainerColor = Color(0xFFF1F2F4),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it.trim()
                        isEmailError = email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    },
                    placeholder = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = fieldShape,
                    isError = isEmailError,
                    supportingText = {
                        if (isEmailError) {
                            Text(
                                "Format email invalide. Ex: email@email.com",
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
                    value = password,
                    onValueChange = {
                        password = it
                        isPasswordError = password.isNotEmpty() && password.length < 8
                        isConfirmError = confirm.isNotEmpty() && confirm != password
                    },
                    placeholder = { Text("Mot de Passe") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = fieldShape,
                    isError = isPasswordError,
                    supportingText = {
                        if (isPasswordError) {
                            Text("Au moins 8 caractères requis", color = mainColor, style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F6F7),
                        unfocusedContainerColor = Color(0xFFF5F6F7),
                        errorContainerColor = Color(0xFFF5F6F7),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        errorBorderColor = mainColor
                    )
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirm,
                    onValueChange = {
                        confirm = it
                        isConfirmError = confirm.isNotEmpty() && confirm != password
                    },
                    placeholder = { Text("Confirmer Mot de Passe") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = fieldShape,
                    isError = isConfirmError,
                    supportingText = {
                        if (isConfirmError) {
                            Text("Les mots de passe ne correspondent pas", color = mainColor, style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F6F7),
                        unfocusedContainerColor = Color(0xFFF5F6F7),
                        errorContainerColor = Color(0xFFF5F6F7),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        errorBorderColor = mainColor
                    )
                )

                Spacer(Modifier.height(22.dp))

                // Bouton inscription appelant Firebase via ViewModel
                Button(
                    onClick = {
                        isEmailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        isPasswordError = password.length < 8
                        isConfirmError = confirm != password

                        if (!isEmailError && !isPasswordError && !isConfirmError) {
                            vm.signUp(email, password)
                        }
                    },
                    enabled = !ui.loading,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mainColor)
                ) {
                    if (ui.loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("S'inscrire", color = Color.White, style = MaterialTheme.typography.titleMedium)
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

                Spacer(Modifier.height(18.dp))

                Row {
                    Text("Vous avez déjà un compte ?", color = Color.Gray)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Se connecter",
                        color = mainColor,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable {
                            navController.navigate(AppRoute.SignIn.route)
                        }
                    )
                }

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}
