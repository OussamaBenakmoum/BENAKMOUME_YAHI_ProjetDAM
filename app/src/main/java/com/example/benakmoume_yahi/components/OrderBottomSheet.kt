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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.benakmoume_yahi.navigation.AppRoute
import com.example.benakmoume_yahi.viewmodel.RestaurantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBottomSheet(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: RestaurantViewModel = viewModel(), // ou passer un viewModel approprié
) {
    var showOrderBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val restaurants by viewModel.restaurants.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = {
                showOrderBottomSheet = true
                viewModel.loadRestaurants()  // charger au clic
            },
            Modifier.height(60.dp).width(250.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6E41),
                contentColor = Color.White
            )
        ) {
            Text("Commander maintenant !")
        }

        if (showOrderBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 30.dp),
                sheetState = sheetState,
                onDismissRequest = { showOrderBottomSheet = false }
            ) {
                if (restaurants.isEmpty()) {
                    Text("Aucun restaurant trouvé.", modifier = Modifier.padding(16.dp))
                } else {
                    Column {
                        restaurants.forEach { restaurant ->
                            Text(
                                text = restaurant.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clickable {
                                        navController.navigate(
                                            AppRoute.RestaurantDetail.createRoute(restaurant.id)
                                        )
                                    }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
