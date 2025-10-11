package com.example.benakmoume_yahi.components

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderBottomSheet(modifier: Modifier = Modifier) {
    var showOrderBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = { showOrderBottomSheet = true },
            Modifier.height(60.dp).width(250.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6E41),
                contentColor = Color.White
            )
        ) {
            Row (Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically){
                /*Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Rate Receipt",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )*/
                Text("Commander maintenant !")

            }
        }

        if (showOrderBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight().padding(0.dp,30.dp, 0.dp, 0.dp),
                sheetState = sheetState,
                onDismissRequest = { showOrderBottomSheet = false }
            ) {
                Text(
                    "Swipe up to open sheet. Swipe down to dismiss.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}