package com.example.benakmoume_yahi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val stringInstruction = "For the Big Mac sauce, combine all the ingredients in a bowl, season with salt and chill until ready to use."

@Composable
fun InstructionCard(stringInstruction: String)
{
    Card(colors = CardDefaults.cardColors(
        containerColor = Color(0xffFFF1EC) // Example: light orange background
    ), modifier = Modifier.padding(vertical = 5.dp)) {
        Text(
            stringInstruction, modifier = Modifier.padding(8.dp)
        )
    }


}

@Preview
@Composable
fun PreviewInstructionCard()
{
    InstructionCard(stringInstruction)
}
