package com.example.benakmoume_yahi.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.benakmoume_yahi.navigation.AppRoute


@Preview(showBackground = true)
@Composable
fun LandingScreenPreview()
{
    val navController = rememberNavController()
    LandingScreen(navController = navController)
}

@Composable
fun LandingScreen(navController: NavHostController, modifier: Modifier = Modifier)
{
    Column(modifier = modifier.background(Color.Transparent).fillMaxSize())
    {
        Text("Landing screen !")
        Button(onClick = {navController.navigate(AppRoute.RecipeDetail.route)}) { }
    }


}