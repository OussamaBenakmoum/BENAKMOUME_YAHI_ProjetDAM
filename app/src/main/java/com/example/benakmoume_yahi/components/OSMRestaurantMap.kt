package com.example.benakmoume_yahi.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.benakmoume_yahi.models.Restaurant
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMRestaurantMap(
    latitude: Double,
    longitude: Double,
    restaurantName: String,
    modifier: Modifier = Modifier,
    listRestaurants: List<Restaurant>
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

                val startPoint = GeoPoint(45.764043, 4.835659)//GeoPoint(latitude, longitude)
                controller.setZoom(15.0)
                controller.setCenter(startPoint)

                Log.d("ici1", listRestaurants.size.toString())

                listRestaurants.forEach { restaurant ->
                    Log.d("ici", restaurant.name)
                    val marker = Marker(this)
                    marker.position = GeoPoint(restaurant.latitude, restaurant.longitude)
                    marker.title = restaurant.name
                    marker.snippet = "Restaurant ID: ${restaurant.id}"

                    marker.setOnMarkerClickListener { _, _ ->
                        val gmmIntentUri = Uri.parse(
                            "google.navigation:q=${restaurant.latitude},${restaurant.longitude}"
                        )
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
            }
        },
        modifier = modifier
    )
}
