package com.example.benakmoume_yahi.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.benakmoume_yahi.models.Restaurant
import com.example.benakmoume_yahi.viewmodel.RestaurantDetailViewModel
import com.example.benakmoume_yahi.viewmodel.RestaurantUiState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun RestaurantDetailScreen(
    navController: NavHostController,
    restaurantId: Int
) {
    val viewModel: RestaurantDetailViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

        // Charger le restaurant au démarrage
        LaunchedEffect(restaurantId) {
            viewModel.loadRestaurantById(restaurantId)
        }

        when (val state = uiState) {
            is RestaurantUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RestaurantUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Erreur: ${state.message}",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadRestaurantById(restaurantId) }) {
                            Text("Réessayer")
                        }
                    }
                }
                LaunchedEffect(Unit) {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }

            is RestaurantUiState.Success -> {
                //Text(state.restaurant.imageRes ?:"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/19/e3/1a/75/la-mere-jean.jpg?w=500&h=-1&s=1")
                RestaurantDetailContent(
                    restaurant = state.restaurant,
                    navController = navController
                )
            }
        }


}

@Composable
fun RestaurantDetailContent(
    restaurant: Restaurant,
    navController: NavHostController
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Image section
        Column(modifier = Modifier.weight(0.25f).fillMaxSize()) {
            /*Image(
                painter = painterResource(restaurant.imageRes),
                contentDescription = restaurant.name,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
                    },
                contentScale = ContentScale.Crop
            )*/
            AsyncImage(
                model = restaurant.imageRes ?:"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/19/e3/1a/75/la-mere-jean.jpg?w=500&h=-1&s=1",
                contentDescription = restaurant.name,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
                    },
                contentScale = ContentScale.Crop
            )
        }

        // Details section
        Column(
            modifier = Modifier
                .weight(0.75f)
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = restaurant.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Approval, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = restaurant.address, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // Afficher le rating si disponible
                    restaurant.rating?.let { rating ->
                        Text(
                            text = "★ $rating/5",
                            fontSize = 16.sp,
                            color = Color(0xFFFF6E41),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(15.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call ${restaurant.phone}",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${restaurant.phone}")
                                }
                                context.startActivity(intent)
                            },
                        tint = Color(0xFFFF6E41)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tags
            restaurant.tags?.let {
                LazyRow(//Mochkil
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(restaurant.tags.size) { index ->
                        val nomTag = restaurant.tags[index]
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xffFFF1EC)
                            )
                        ) {
                            Text(text = nomTag, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }

            // Description si disponible
            restaurant.description?.let { desc ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = desc,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Map
            OSMRestaurantMap(
                latitude = restaurant.latitude,
                longitude = restaurant.longitude,
                restaurantName = restaurant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clipToBounds()
            )
        }
    }
}

@Composable
fun OSMRestaurantMap(
    latitude: Double,
    longitude: Double,
    restaurantName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            Configuration.getInstance().load(
                ctx,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx)
            )
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                val startPoint = GeoPoint(latitude, longitude)
                controller.setZoom(15.0)
                controller.setCenter(startPoint)

                val marker = Marker(this)
                marker.position = startPoint
                marker.title = restaurantName
                marker.setOnMarkerClickListener { _, _ ->
                    val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    }
                    true
                }
                overlays.add(marker)
            }
        },
        modifier = modifier
    )
}
