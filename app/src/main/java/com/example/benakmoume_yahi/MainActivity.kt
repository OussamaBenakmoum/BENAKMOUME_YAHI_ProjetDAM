package com.example.benakmoume_yahi

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.benakmoume_yahi.navigation.AppNavGraph
import com.example.benakmoume_yahi.screens.ChooseCategoryScreen
import com.example.benakmoume_yahi.screens.ChooseCuisineScreen
import com.example.benakmoume_yahi.screens.LandingScreen
import com.example.benakmoume_yahi.screens.SignInScreen
import com.example.benakmoume_yahi.screens.SignUpScreen
import com.example.benakmoume_yahi.screens.WelcomeScreen
import com.example.benakmoume_yahi.ui.theme.BENAKMOUME_YAHITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            BENAKMOUME_YAHITheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(Color.Black)) { innerPadding ->
                    AppNavGraph(modifier = Modifier.padding(innerPadding))
                    //ChooseCuisineScreen(navController)
                    //SignUpScreen(modifier = Modifier.padding(innerPadding))
                    //WelcomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

//[OB] : Call this methods directly instead of Greetings
/*
//WelcomeScreen(modifier = Modifier.padding(innerPadding))
//LoginOrSignUpScreen(modifier = Modifier.padding(innerPadding))
//RecipeDetailScreen(modifier = Modifier.padding(innerPadding))
//SignUpScreen(modifier = Modifier.padding(innerPadding))
SignInScreen(modifier = Modifier.padding(innerPadding))


 */







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






