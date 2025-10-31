package com.example.benakmoume_yahi.screens

import android.R
import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.benakmoume_yahi.models.RestaurantCardData
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.utils.restaurantList


@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen()
{
    val navController = rememberNavController()
    SearchScreen(navController)
}
@Composable
fun SearchScreen(navController: NavHostController) {
    var search by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var showAllRestaurants by remember { mutableStateOf(false) }


    Column(modifier = Modifier
        .fillMaxSize()
        //.padding(vertical = 10.dp)
    )
    {
        Text(
            text = "Search",
            color = Color.Black,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 15.dp)
        )

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            shape = RoundedCornerShape(24.dp),
            //label = { Text("Rechercher ta recette favorite...", modifier = Modifier.background(Color.Transparent)) },
            leadingIcon = {  Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 23.dp, vertical = 10.dp)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF4F2EE),
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.Red,
                unfocusedLabelColor = Color.LightGray,
            )
        )
        Column (modifier = Modifier.background(Color(0xFFF4F2EE)).padding(horizontal = 8.dp).fillMaxWidth())
        {
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween)
            {
                Text(
                    text = "Recettes",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                var i = 0
                Text(
                    text = "Plus +",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp).clickable(true, onClick = {null})
                )



            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            {
                items(recipeList.size) { index ->
                    val recipe = recipeList[index]
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        //backgroundColor = Color.White,
                        //elevation = 4.dp,
                        modifier = Modifier
                            .size(width = 260.dp, height = 170.dp)
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = recipe.imageRes),
                                contentDescription = recipe.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                                    .graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
                                    .drawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.verticalGradient(
                                                colors = listOf( Color.Transparent, Color.Black),
                                            ),
                                        )
                                    },
                            )
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(recipe.category, fontSize = 11.sp, color = Color.White)
                                Text(recipe.title, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.background(Color.White).height(12.dp) )

        Column(modifier = Modifier.background(Color(0xFFF4F2EE)).padding(horizontal = 8.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Restaurants",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            val displayedRestaurants = if (showAllRestaurants) restaurantList else restaurantList.take(2)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedRestaurants.size) { index ->
                    val restaurant = displayedRestaurants[index]
                    Column ()
                    {
                        Row(modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { navController.navigate(AppRoute.RestaurantDetail.Companion.createRoute(restaurant.id)) }
                        )
                        {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(120.dp)

                            ) {
                                Image(
                                    painter = painterResource(id = restaurant.imageRes),
                                    contentDescription = restaurant.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(restaurant.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(restaurant.address, fontSize = 13.sp, color = Color.Gray)
                                Text(
                                    text = restaurant.tags.joinToString(separator = ", "),
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.background(Color.White).height(12.dp) )
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp),
                            thickness = DividerDefaults.Thickness, color = Color(0xFFcfcfcf)
                        )


                    }
                }
            }
            if (!showAllRestaurants && restaurantList.size > 2) {
                Text(
                    text = "Afficher tous les restaurants obtenus",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        //.padding(vertical = 8.dp)
                        .clickable { showAllRestaurants = true }
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

    }
}

data class RecipeCardData(
    val imageRes: Int,
    val category: String,
    val title: String
)
val recipeList = listOf(
    RecipeCardData(
        imageRes = com.example.benakmoume_yahi.R.drawable.platwelcome3, // replace with your resource
        category = "Indonesian Food",
        title = "Java corn with peanut sauce"
    ),
    RecipeCardData(
        imageRes = com.example.benakmoume_yahi.R.drawable.platwelcome3,
        category = "Japanese Food",
        title = "Spaghetti Carbonara"
    ),
    RecipeCardData(
        imageRes = com.example.benakmoume_yahi.R.drawable.platwelcome3,
        category = "Mexican Food",
        title = "Chicken Tacos"
    ),
    RecipeCardData(
        imageRes = com.example.benakmoume_yahi.R.drawable.platwelcome3,
        category = "Moroccan Food",
        title = "Lamb Tagine"
    ),
    RecipeCardData(
        imageRes = com.example.benakmoume_yahi.R.drawable.platwelcome3,
        category = "Italian Food",
        title = "Margherita Pizza"
    )
)


