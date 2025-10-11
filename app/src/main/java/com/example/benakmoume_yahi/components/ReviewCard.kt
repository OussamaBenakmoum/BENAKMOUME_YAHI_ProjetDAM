package com.example.benakmoume_yahi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.benakmoume_yahi.models.Review

@Composable
fun ReviewCard(review: Review)
{

    Row(modifier = Modifier.padding(vertical = 10.dp))
    {
        AsyncImage(
            model = review.userImageUrl,
            contentDescription = "User profile picture",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(0.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
        )
        Column (modifier = Modifier.padding(horizontal = 5.dp))
        {
            Row(modifier = Modifier.fillMaxWidth())
            {
                Column ()//will hold username and date
                {
                    Text(text = review.userName, color = Color.Black, fontWeight = FontWeight.SemiBold, lineHeight = 12.sp )
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(text = review.reviewAt, color = Color.Gray, fontWeight = FontWeight.Light, fontSize = 12.sp, lineHeight = 12.sp )
                }
                Row (modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) // will hold rating stars
                {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rate Receipt",
                        tint = Color(0xFFFF6E41),
                        modifier = Modifier.size(15.dp),
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rate Receipt",
                        tint = Color(0xFFFF6E41),
                        modifier = Modifier.size(15.dp),

                        )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rate Receipt",
                        tint = Color(0xFFFF6E41),
                        modifier = Modifier.size(15.dp),

                        )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rate Receipt",
                        tint = Color(0xFFFF6E41),
                        modifier = Modifier.size(15.dp),

                        )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rate Receipt",
                        tint = Color.Gray,
                        modifier = Modifier.size(15.dp),

                        )
                }
            }
            Text(text = review.textReview)
        }
    }


}

val reviewExample =     Review( 1, "https://t4.ftcdn.net/jpg/03/83/25/83/360_F_383258331_D8imaEMl8Q3lf7EKU2Pi78Cn0R7KkW9o.jpg","John Doe","The dish was absolutely delicious! Fresh ingredients and perfect seasoning. Highly recommend it.", "30 minutes ago",rating = 5)


@Preview(showBackground = true)
@Composable
fun ReviewCardPreview()
{

    ReviewCard(reviewExample)
}