package com.example.benakmoume_yahi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun RestaurantCard(
    restaurant: com.example.benakmoume_yahi.models.Restaurant,
    onClick: () -> Unit
)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(100.dp)
                .width(120.dp)
        ) {
            /*Image(
                painter = painterResource(id = com.example.benakmoume_yahi.R.drawable.platwelcome3), // À remplacer
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )*/
            AsyncImage(
                model = restaurant.imageRes ?:"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/19/e3/1a/75/la-mere-jean.jpg?w=500&h=-1&s=1",
                contentDescription = restaurant.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 12.dp, top = 5.dp, bottom = 5.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(restaurant.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(restaurant.address ?: "", fontSize = 13.sp, color = Color.Gray)
            Text(
                text = "★ ${restaurant.rating}/5",
                fontSize = 16.sp,
                color = Color(0xFFFF6E41),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = restaurant.tags?.joinToString(separator = ", ") ?: "",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}