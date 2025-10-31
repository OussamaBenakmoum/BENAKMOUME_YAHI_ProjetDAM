package com.example.benakmoume_yahi.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.components.ReviewCard
import com.example.benakmoume_yahi.models.Restaurant
import com.example.benakmoume_yahi.utils.reviews
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun RestaurantDetailScreen(navController: NavHostController, restaurant : Restaurant)
{
    val context = LocalContext.current

    Column (modifier = Modifier.fillMaxSize()) {
        Column (modifier = Modifier.weight(0.25f).fillMaxSize())
        {
            Image(
                painter = painterResource(restaurant.imageRes),
                contentDescription = restaurant.name,
                modifier = Modifier.fillMaxSize()
                    .graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen },
                contentScale = ContentScale.Crop
            )
        }

        Column (modifier = Modifier.weight(0.75f).fillMaxSize().padding(10.dp))
        {
            Row (modifier = Modifier.fillMaxWidth())
            {
                Column (modifier = Modifier)
                {
                    Text(text = restaurant.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row (verticalAlignment = Alignment.CenterVertically)
                    {
                        Icon(Icons.Default.Approval, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = restaurant.address, fontSize = 16.sp)
                    }
                }
                Column (
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                )
                {
                    Icon(
                        imageVector = Icons.Default.Phone, // or painterResource(R.drawable.ic_phone)
                        contentDescription = "Call ${"0605787761"}",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$0605787761")
                                }
                                context.startActivity(intent)
                            },
                        tint = Color(0xFFFF6E41)
                    )
                }


            }

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow (horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp))
            {
                items(restaurant.tags.size) { index ->
                    val nomTag = restaurant.tags[index]
                    Card (colors = CardDefaults.cardColors(
                        containerColor = Color(0xffFFF1EC) // Example: light orange background
                    )){
                        Text(text = nomTag, modifier = Modifier.padding(8.dp))
                    }

                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            OSMRestaurantMap(
                latitude = restaurant.latitude,
                longitude = restaurant.longitude,
                restaurantName = restaurant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp).clipToBounds()
            )



        }



        /*Image(
            painter = painterResource(id = restaurant.imageRes),
            contentDescription = restaurant.name,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )*/



    }
}


@Composable
fun OSMRestaurantMap(
    latitude: Double,
    longitude: Double,
    restaurantName: String,
    modifier: Modifier = Modifier)
{
    val context = androidx.compose.ui.platform.LocalContext.current

    AndroidView(
        factory = { ctx ->
            Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
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


@Preview(showBackground = true)
@Composable
fun RestaurantDetailScreenPreview() {
    val sampleRestaurant = Restaurant(
        id = 1,
        name = "Culina Hortus",
        tags = listOf("gastronomique", "végétarien", "fait maison", "végétarien", "fait maison", "végétarien", "fait maison"),
        imageRes = android.R.drawable.ic_menu_gallery, // Use your drawable or system one for preview
        address = "38 rue de l’Arbre sec, 69001 Lyon",
        latitude = 45.767,
        longitude = 4.834
    )
    RestaurantDetailScreen(
        navController = rememberNavController(),
        restaurant = sampleRestaurant
    )
}