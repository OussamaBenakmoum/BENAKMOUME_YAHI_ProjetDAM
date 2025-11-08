package com.example.benakmoume_yahi

//import android.content.Intent
//import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.benakmoume_yahi.navigation.AppNavGraph
import com.example.benakmoume_yahi.screens.SignInScreen
import com.example.benakmoume_yahi.screens.SignUpScreen
import com.example.benakmoume_yahi.screens.WelcomeScreen
import com.example.benakmoume_yahi.ui.theme.BENAKMOUME_YAHITheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BENAKMOUME_YAHITheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )

                    //SignUpScreen(modifier = Modifier.padding(innerPadding))
                    //WelcomeScreen(modifier = Modifier.padding(innerPadding))
                    //SignInScreen(modifier = Modifier.padding(innerPadding))
                }

                // Gérer le deep link à l'ouverture
//                LaunchedEffect(Unit) {
//                    val data: Uri? = intent?.data
//                    val oobCode = data?.getQueryParameter("oobCode")
//                    if (oobCode != null) {
//                        navController.navigate("newPassword?oobCode=$oobCode")
//                    }
//                }
            }
        }
    }

    // Si l'activité est déjà ouverte et reçoit un nouvel Intent (clic d’email)
    //override fun onNewIntent(intent: Intent?) {
      //  super.onNewIntent(intent)
        //setIntent(intent)
  //  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier)
{
    Column (modifier = Modifier.fillMaxSize().background(Color.Red)){
        Text("sss")
    }
}






@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BENAKMOUME_YAHITheme {
        //Greeting("Android")
    }
}






