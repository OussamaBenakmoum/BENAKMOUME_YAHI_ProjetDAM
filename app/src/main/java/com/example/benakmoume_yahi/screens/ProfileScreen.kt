package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.navigation.AppRoute

@Composable
fun ProfileScreen(navController: NavHostController)
{
    Column(modifier = Modifier.fillMaxSize())
    {
        Text("Profile screen !")
        Button(onClick = {navController.navigate(AppRoute.RecipeDetail.Companion.createRoute("52771"))}) { }

    }
}
