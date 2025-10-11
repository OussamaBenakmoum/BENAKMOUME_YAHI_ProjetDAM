package com.example.benakmoume_yahi.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.components.OrderBottomSheet
import com.example.benakmoume_yahi.components.ReviewCard
import com.example.benakmoume_yahi.components.YouTubePlayer
import com.example.benakmoume_yahi.ui.theme.BENAKMOUME_YAHITheme
import com.example.benakmoume_yahi.utils.hasInternet
import com.example.benakmoume_yahi.utils.ingredients
import com.example.benakmoume_yahi.utils.reviews

@Preview(showBackground = true)
@Composable
fun RecipeDetailScreenPreview() {
    BENAKMOUME_YAHITheme {
        RecipeDetailScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(modifier: Modifier = Modifier)
{
    var showPlayer by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Ingrédients", "Instructions", "Avis")
    val scrollState = rememberScrollState()

    var showOrderBottomSheet by remember { mutableStateOf(false) }
    val ratingSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    Column (modifier = Modifier.fillMaxSize().padding(0.dp, 30.dp, 0.dp, 50.dp))
    {
        Column (modifier = Modifier.fillMaxWidth().weight(0.25f))
        {
            if (showPlayer && hasInternet(context)) {
                YouTubePlayer(videoId = "C5J39YnnPsg")
            } else {
                Box(modifier = Modifier.fillMaxSize()) {

                    if (hasInternet(context)) {
                        AsyncImage(
                            model = "https://www.themealdb.com/images/media/meals/urzj1d1587670726.jpg",
                            contentDescription = "Meal Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
                                .drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color.White, Color.White),
                                            startY = 0f,
                                            endY = size.height / 7
                                        ),
                                        blendMode = BlendMode.DstIn
                                    )
                                },
                            contentScale = ContentScale.Crop
                        )
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Video",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(64.dp)
                                .clickable {
                                    if (hasInternet(context)) {
                                        showPlayer = true
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Check internet connection",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        )
                    }
                    else {
                        Image(
                            painter = painterResource(R.drawable.platwelcome3),
                            contentDescription = "Default Meal Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
                                .drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.White),
                                            startY = 0f,
                                            endY = size.height / 7
                                        ),
                                        blendMode = BlendMode.DstIn
                                    )
                                },
                            contentScale = ContentScale.Crop
                        )
                    }

                }
            }
        }
        Column (modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth().weight(0.14f)){
            Column (modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp))
            {
                Text("Java corn with peanut sauce",
                    fontSize = 27.sp)
                Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp))
                {
                    Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                        showOrderBottomSheet = true
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rate Receipt",
                            tint = Color(0xFFFF6E41),
                            modifier = Modifier.size(18.dp),
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rate Receipt",
                            tint = Color(0xFFFF6E41),
                            modifier = Modifier.size(18.dp),

                            )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rate Receipt",
                            tint = Color(0xFFFF6E41),
                            modifier = Modifier.size(18.dp),

                            )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rate Receipt",
                            tint = Color(0xFFFF6E41),
                            modifier = Modifier.size(18.dp),

                            )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rate Receipt",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp),

                            )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("14,991 reviews", color = Color.Gray)
                    }
                    Row (verticalAlignment = Alignment.CenterVertically)
                    {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Rate Receipt",
                            tint = Color(0xFFFF6E41),
                            modifier = Modifier.size(18.dp),

                            )
                        Text(" Favori", color = Color.Gray)
                    }

                }
            }
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround)
            {
                Column()
                {
                    Text("Prep. Time", fontSize = 13.sp, color = Color.Gray)
                    Text("45 min", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column()
                {
                    Text("Cook Time", fontSize = 13.sp, color = Color.Gray)
                    Text("10 min", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column()
                {
                    Text("Cooked", fontSize = 13.sp, color = Color.Gray)
                    Text("9.5K", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column()
                {
                    Text("Level", fontSize = 13.sp, color = Color.Gray)
                    Text("Easy", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            thickness = DividerDefaults.Thickness, color = Color(0xFFcfcfcf)
        )
        Box(modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth().weight(0.5f))
        {
            Column (modifier = Modifier/*.weight(0.5f).*/.verticalScroll(scrollState))
            {
                Spacer(modifier = Modifier.height(10.dp))

                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(selectedTab),
                            height = 0.dp,
                            color = Color.White
                        )
                    },
                    divider = {
                        Divider(color = Color.White, thickness = 0.dp)
                    }
                )
                {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedTab == index) Color(0xffFFF1EC) else Color.Transparent,
                                        shape = RoundedCornerShape(50) // optional: rounded background
                                    )
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = title,
                                    color = if (selectedTab == index) Color(0xFFFF6E41) else Color.Gray,
                                    fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                when (selectedTab)
                {
                    0 -> {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                        )
                        {
                            ingredients.forEach { ingredient ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {

                                    AsyncImage(
                                        model = ingredient.imageUrl,
                                        contentDescription = ingredient.name,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .padding(end = 8.dp)
                                    )
                                    // Texte quantité et nom
                                    Text(
                                        text = "${ingredient.amount} ${ingredient.name}",
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                    }
                    1 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                            //.verticalScroll(scrollState)
                        )
                        {
                            Text(
                                "For the Big Mac sauce, combine all the ingredients in a bowl, season with salt and chill until ready to use.\r\n2. To make the patties, season the mince with salt and pepper and form into 4 balls using about 1/3 cup mince each. Place each onto a square of baking paper and flatten to form into four x 15cm circles. Heat oil in a large frypan over high heat. In 2 batches, cook beef patties for 1-2 minutes each side until lightly charred and cooked through. Remove from heat and keep warm. Repeat with remaining two patties.\r\n3. Carefully slice each burger bun into three acrossways, then lightly toast.\r\n4. To assemble the burgers, spread a little Big Mac sauce over the bottom base. Top with some chopped onion, shredded lettuce, slice of cheese, beef patty and some pickle slices. Top with the middle bun layer, and spread with more Big Mac sauce, onion, lettuce, pickles, beef patty and then finish with more sauce. Top with burger lid to serve.\r\n5. After waiting half an hour for your food to settle, go for a jog."
                            )

                        }
                    }
                    2 -> {
                        reviews.forEach {
                            ReviewCard(it)
                        }
                    }
                }


            }

            OrderBottomSheet()

            if (showOrderBottomSheet) {
                var selectedRating by remember { mutableStateOf(2) }
                var reviewText by remember { mutableStateOf("") }
                ModalBottomSheet(
                    modifier = Modifier.fillMaxHeight().padding(0.dp,30.dp, 0.dp, 0.dp),
                    sheetState = ratingSheetState,
                    onDismissRequest = { showOrderBottomSheet = false }
                ) {

                    // Title
                    Text(
                        text = "Do you like this recipe video?",
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    // Spacer
                    Spacer(Modifier.height(16.dp))
                    // Stars
                    Row(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rate",
                                tint = if (index < selectedRating) Color(0xFFFFBD4A) else Color(0xFFE0E0E0),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { selectedRating = index + 1 }
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    // Review text field
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        placeholder = { Text("Type your review here...") },
                        shape = RoundedCornerShape(16.dp),
                        minLines = 3,
                        maxLines = 5
                    )
                    Spacer(Modifier.height(24.dp))
                    // Give Review Button
                    Button(
                        onClick = { /* Submit action */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6E41)
                        )
                    ) {
                        Text("Give Review")
                    }
                    // Maybe Later
                    TextButton(
                        onClick = { showOrderBottomSheet = false },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Maybe Later", color = Color.Gray)
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

        }
    }
}
