package com.example.benakmoume_yahi.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.models.Recipe

val rec: Recipe = Recipe("ss","Ma recette bonne","Breakfast", "French", "url", "url", "tags", emptyList(),emptyList(),null)
val mainColor = Color(0xFFFF6E41)
@Composable
@Preview
fun RecipeCardPreview() {
    RecipeCard(rec, onClick = {})
}

@Composable
fun RecipeCard(recipe: com.example.benakmoume_yahi.models.Recipe, Airecommended: Boolean = false, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (Airecommended) mainColor else Color.Black),
        modifier = Modifier
            .size(width = 260.dp, height = 170.dp)
            .clickable { onClick() }
    ) {
        Box(Modifier.fillMaxSize()) {
            // Utiliser AsyncImage si vous avez une URL, sinon painterResource
            AsyncImage(
                model = recipe.image_url,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
                    }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                 colors = listOf(Color.Transparent, Color.Black)
                            ),
                        )
                    },
            )

            if (Airecommended) {
                Card(
                    modifier = Modifier.padding(10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(0.dp, Color.Transparent)
                )
                {
                    Image(
                        painter = painterResource(R.drawable.aistars),
                        contentDescription = "test",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row ()
                {
                    Card (
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, color = mainColor))
                    {
                        Text(recipe.area ?: "", fontSize = 11.sp, color = mainColor, modifier = Modifier.padding(5.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card (shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, color = mainColor))
                    {

                        Text(recipe.category ?: "", fontSize = 11.sp, color = mainColor, modifier = Modifier.padding(5.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(recipe.name, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
