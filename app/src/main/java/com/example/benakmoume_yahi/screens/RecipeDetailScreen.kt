package com.example.benakmoume_yahi.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.benakmoume_yahi.Auth.AuthRepository
import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.components.InstructionCard
import com.example.benakmoume_yahi.components.OrderBottomSheet
import com.example.benakmoume_yahi.components.RecipeCard
import com.example.benakmoume_yahi.components.ReviewCard
import com.example.benakmoume_yahi.components.YouTubePlayer
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.ui.theme.BENAKMOUME_YAHITheme
import com.example.benakmoume_yahi.viewmodel.RecipeDetailViewModel
import com.example.benakmoume_yahi.viewmodel.RecipeUiState
import com.example.benakmoume_yahi.utils.hasInternet
import com.example.benakmoume_yahi.utils.ingredients
import com.example.benakmoume_yahi.utils.reviews
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

val stringInstruction = "For the Big Mac sauce, combine all the ingredients in a bowl, season with salt and chill until ready to use."

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Recipe Detail Preview"
)
@Composable
fun RecipeDetailScreenPreview() {
    val fakeNavController = rememberNavController()
    MaterialTheme {
        RecipeDetailScreen(navController = fakeNavController,"")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavHostController,
    mealId: String?,
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showPlayer by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Ingrédients", "Instructions", "Avis")
    val scrollState = rememberScrollState()

    var showOrderBottomSheet by remember { mutableStateOf(false) }
    val ratingSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    val authRepo = AuthRepository()
    val currentUser by authRepo.currentUserFlow.collectAsState()

    var isFavorite by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val comments by viewModel.comments.collectAsState()

    // Charger la recette au démarrage
    LaunchedEffect(mealId) {
        if (mealId != null) {
            viewModel.loadRecipeById(mealId)
            isFavorite = viewModel.isRecipeFavorite(
                currentUser?.uid ?:"",
                idMeal =  mealId)//isRecipeFavorite("pSgIsaYVwWY32kq5HFuuTio06332", "52772")
        } else {
            //viewModel.loadRandomRecipe()
            viewModel.loadRecipeById("53085")

        }
    }

    // Récupérer le nom de la recette depuis l'API
    val recipeId = when (val state = uiState) {
        is RecipeUiState.Success -> state.recipe.id_meal
        else -> "null" // Valeur par défaut
    }

    val recipeName = when (val state = uiState) {
        is RecipeUiState.Success -> state.recipe.name
        else -> "Java corn with peanut sauce" // Valeur par défaut
    }

    val recipeYoutubeUrl = when (val state = uiState) {
        is RecipeUiState.Success -> state.recipe.youtube_url
        else -> null
    }

    val recipeImageUrl = when (val state = uiState) {
        is RecipeUiState.Success -> state.recipe.image_url
        else -> null
    }

    val ingredientsList = when (val state = uiState) {
        is RecipeUiState.Success -> state.recipe.ingredients
        else -> ingredients
    }

    val instructionsList = when (val state = uiState) {
        is RecipeUiState.Success -> state.recipe.instructions
        else -> listOf(stringInstruction, stringInstruction, stringInstruction)
    }



    Column(modifier = Modifier.fillMaxSize().padding(0.dp, 0.dp, 0.dp, 0.dp))
    {
        Column(modifier = Modifier.fillMaxWidth().weight(0.25f))
        {
            if (showPlayer && hasInternet(context)) {
                YouTubePlayer(videoId = recipeYoutubeUrl?:"k9Ez0bUbXKc")
            } else {
                Box(modifier = Modifier.fillMaxSize()) {

                    if (hasInternet(context)) {
                        AsyncImage(
                            model = recipeImageUrl,
                            contentDescription = "Meal Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
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
                                        Toast
                                            .makeText(
                                                context,
                                                "Check internet connection",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.platwelcome3),
                            contentDescription = "Default Meal Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
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
        Column(modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth().weight(0.14f)) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp))
            {
                Text(
                    text = recipeName,
                    fontSize = 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                )
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                if (isFavorite) {
                                    val result = viewModel.removeRecipeFromFavorites(currentUser?.uid ?: "", recipeId)
                                    if (result) {
                                        isFavorite = false           // <--- Mise à jour de l'état
                                        Toast.makeText(context, "Favori retiré", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Erreur lors du retrait", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val result = viewModel.addRecipeToFavorites(currentUser?.uid ?: "", recipeId)
                                    if (result) {
                                        isFavorite = true            // <--- Mise à jour de l'état
                                        Toast.makeText(context, "Ajouté aux favoris", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    ) {

                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Ajouter aux favoris",
                            tint = Color(0xFFFF6E41),
                            modifier = Modifier.size(18.dp),
                        )

                        Text(" Favori", color = Color.Gray)
                    }

                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            )
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
            Column(modifier = Modifier/*.weight(0.5f).*/.verticalScroll(scrollState))
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        {
                            ingredientsList.forEach { ingredient ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {

                                    AsyncImage(
                                        model = ingredient.image_url,
                                        contentDescription = ingredient.name,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .padding(end = 8.dp)
                                    )
                                    // Texte quantité et nom
                                    Text(
                                        text = "${ingredient.measure} ${ingredient.name}",
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
                            instructionsList.forEach { element ->
                                InstructionCard(element)
                            }

                        }
                    }
                    2 -> {
                        if (comments.isEmpty()) {
                            Text("Aucun commentaire disponible", modifier = Modifier.padding(16.dp))
                        } else {
                            Text(text = comments.size.toString())

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                items(comments.size) { index ->
                                    val reviw = comments.get(index)
                                    ReviewCard(reviw)
                                    //Text(reviw.comment_text)
                                }
                            }

                        }
                    }
                }


            }

            OrderBottomSheet()

            if (showOrderBottomSheet) {
                var selectedRating by remember { mutableStateOf(2) }
                var reviewText by remember { mutableStateOf("") }
                ModalBottomSheet(
                    modifier = Modifier.fillMaxHeight().padding(0.dp, 30.dp, 0.dp, 0.dp),
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
