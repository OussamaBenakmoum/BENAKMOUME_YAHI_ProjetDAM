package com.example.benakmoume_yahi.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.ui.theme.BENAKMOUME_YAHITheme

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    BENAKMOUME_YAHITheme {
        WelcomeScreen()
    }
}
@Composable
fun WelcomeScreen(modifier: Modifier = Modifier)
{
    val mainColor = Color(0xFFFF6E41)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 40.dp, 0.dp, 80.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column (modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,


        ) {

            Text(
                text = "GoGourmet",
                style = MaterialTheme.typography.headlineMedium,
                color = mainColor
            )
            Text(
                text = "Découvrez, cuisinez, commandez, savourez.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.platwelcome3),
                contentDescription = "Product Image",

                modifier = Modifier.fillMaxWidth()
                    .graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.White),
                                startY = 0f,
                                endY = size.height / 3
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    }
                    .clip(RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 80.dp,
                        bottomEnd = 0.dp
                    ))

            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 18.dp, horizontal = 22.dp)
        ) {
            Text(
                text = "Prêt à ravir les papilles?",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explorez des saveurs authentiques avec des recettes faciles et un guide pas à pas, ou profitez des restaurants proches de chez vous. ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(22.dp))
            Button(
                onClick = { println("Start Cooking") },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mainColor)
            ) {
                Text(
                    text = "C’est parti !",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }

        }

    }
}