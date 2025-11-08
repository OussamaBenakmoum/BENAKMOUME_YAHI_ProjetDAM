package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.Auth.AuthViewModel
import com.example.benakmoume_yahi.navigation.AppRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleSignUpScreen(
    nav: NavHostController,
    vm: AuthViewModel = viewModel()
) {
    val mainColor = Color(0xFFFF6E41)
    val fieldShape = RoundedCornerShape(22.dp)

    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val isValid = first.isNotBlank() && last.isNotBlank() && !saving

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Compléter le profil", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { if (!saving) nav.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Prénom
            OutlinedTextField(
                value = first,
                onValueChange = { first = it },
                placeholder = { Text("Prénom") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = fieldShape,
                enabled = !saving,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F2F4),
                    unfocusedContainerColor = Color(0xFFF1F2F4),
                    disabledContainerColor = Color(0xFFF1F2F4),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(12.dp))

            // Nom
            OutlinedTextField(
                value = last,
                onValueChange = { last = it },
                placeholder = { Text("Nom") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = fieldShape,
                enabled = !saving,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F2F4),
                    unfocusedContainerColor = Color(0xFFF1F2F4),
                    disabledContainerColor = Color(0xFFF1F2F4),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                )
            )

            if (localError != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    localError!!,
                    color = mainColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {
                    saving = true
                    localError = null
                    vm.completeGoogleProfile(
                        firstName = first.trim(),
                        lastName = last.trim(),
                        onDone = {
                            saving = false
                            nav.navigate(AppRoute.ChooseCuisine.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onError = { msg ->
                            saving = false
                            localError = msg ?: "Erreur inattendue"
                        }
                    )
                },
                enabled = isValid,
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
                if (saving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Continuer", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
