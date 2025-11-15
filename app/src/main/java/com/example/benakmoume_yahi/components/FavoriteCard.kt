package com.example.benakmoume_yahi.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.benakmoume_yahi.models.Recipe

@Composable
fun FavoriteCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
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
            AsyncImage(
                model = recipe.image_url
                    ?: "https://images.unsplash.com/photo-1498579809087-ef1e558fd1da?w=640",
                contentDescription = recipe.name.ifBlank { "Recipe" },
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
            Text(
                text = recipe.name.ifBlank { "Recette" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 2
            )

            val subtitle = listOfNotNull(
                recipe.area?.takeIf { it.isNotBlank() },
                recipe.category?.takeIf { it.isNotBlank() }
            ).joinToString(" • ")
            if (subtitle.isNotEmpty()) {
                Text(text = subtitle, fontSize = 13.sp, color = Color.Gray)
            }

            val tagsLine = recipe.tags?.takeIf { it.isNotBlank() } ?: ""
            if (tagsLine.isNotEmpty()) {
                Text(text = tagsLine, fontSize = 13.sp, color = Color.Gray, maxLines = 1)
            }
        }
    }
}
