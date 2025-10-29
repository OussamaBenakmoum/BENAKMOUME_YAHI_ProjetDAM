package com.example.benakmoume_yahi.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.benakmoume_yahi.models.Restaurant
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun RestaurantDetailScreen(navController: NavHostController, restaurant : Restaurant)
{
    Column {
        Text(text = restaurant.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = restaurant.address)
        Text(text = "Tags: ${restaurant.tags.joinToString()}")
        Image(
            painter = painterResource(id = restaurant.imageRes),
            contentDescription = restaurant.name,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OSMRestaurantMap(
            latitude = restaurant.latitude,
            longitude = restaurant.longitude,
            restaurantName = restaurant.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp).background(Color.Red)
        )
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
        tags = listOf("gastronomique", "végétarien", "fait maison"),
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