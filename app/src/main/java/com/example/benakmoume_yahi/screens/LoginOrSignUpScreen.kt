package com.example.benakmoume_yahi.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.navigation.AppRoute
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.benakmoume_yahi.Auth.AuthViewModel

@Composable
fun LoginOrSignUpScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: AuthViewModel = viewModel()
) {
    val mainColor = Color(0xFFFF6E41)
    val context = LocalContext.current
    val activity = context as Activity
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }
    val credentialManager = remember { CredentialManager.create(context) }
    val webClientId = stringResource(id = R.string.default_web_client_id)

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                painter = painterResource(id = R.drawable.platwelcome3),
                contentDescription = "Header",
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.White),
                                startY = 0f,
                                endY = size.height / 3f
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    }
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp, topEnd = 0.dp,
                            bottomStart = 80.dp, bottomEnd = 0.dp
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                Text(
                    text = "Créez votre compte",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
                Text(
                    text = "Obtenez 3 cours de cuisine gratuits chaque jour",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bouton Google
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setServerClientId(webClientId)
                                    .setFilterByAuthorizedAccounts(false)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result: GetCredentialResponse =
                                    credentialManager.getCredential(activity, request)

                                val cred = result.credential
                                if (cred is CustomCredential &&
                                    cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val idToken = GoogleIdTokenCredential
                                        .createFrom(cred.data)
                                        .idToken
                                    val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)

                                    // 1) Auth Firebase
                                    auth.signInWithCredential(firebaseCred).await()

                                    // 2) Guard profil -> route (ne pas aller directement au Landing)
                                    vm.onGoogleAuthSuccess(
                                        navToComplete = {
                                            navController.navigate(AppRoute.GoogleSignUp.route) {
                                                launchSingleTop = true
                                            }
                                        },
                                        navNext = {
                                            navController.navigate(AppRoute.ChooseCuisine.route) {
                                                popUpTo(0) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                } else {
                                    snackbar.showSnackbar("Échec Google: credential inattendu")
                                }
                            } catch (e: Exception) {
                                snackbar.showSnackbar(e.message ?: "Échec de la connexion Google")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F3F4))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.googlelogo),
                            contentDescription = "Google logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continuer avec Google",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Bouton Email -> navigation vers SignUp
                Button(
                    onClick = {
                        navController.navigate(AppRoute.SignUp.route) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mainColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.emaillogo),
                            contentDescription = "Email logo",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continuer avec Email",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                Text(
                    text = "Vous avez déjà un compte ?  ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 18.sp
                )
                Text(
                    text = "Se connecter",
                    style = MaterialTheme.typography.headlineSmall,
                    color = mainColor,
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(AppRoute.SignIn.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
