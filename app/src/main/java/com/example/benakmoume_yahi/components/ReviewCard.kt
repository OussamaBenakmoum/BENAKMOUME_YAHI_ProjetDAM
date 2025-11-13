package com.example.benakmoume_yahi.components

import android.R
import android.widget.Toast
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.benakmoume_yahi.models.RecipeCommentWithUser
import com.example.benakmoume_yahi.models.Review
import com.example.benakmoume_yahi.utils.formatTimeDifference

@Composable
fun ReviewCard(review: RecipeCommentWithUser, myComment:Boolean = false, onDelete: (commentId: Int) -> Unit = {})
{
    val context = LocalContext.current


    Row(modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Absolute.SpaceBetween)
    {
        AsyncImage(
            model = review.user_photo ?: "https://randomuser.me/api/portraits/women/68.jpg",
            contentDescription = "User profile picture",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(0.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
        )

        Column (modifier = Modifier.padding(horizontal = 5.dp))
        {
            Row(/*modifier = Modifier.fillMaxWidth(),*/ horizontalArrangement = Arrangement.SpaceEvenly)
            {
                Column (/*modifier = Modifier.fillMaxWidth()*/)//will hold username and date
                {
                    Text(text = review.user_firstname +" "+ review.user_lastname ?: "", color = Color.Black, fontWeight = FontWeight.SemiBold, lineHeight = 12.sp )
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(text = formatTimeDifference( review.created_at ?: ""), color = Color.Gray, fontWeight = FontWeight.Light, fontSize = 12.sp, lineHeight = 12.sp )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row (/*modifier = Modifier.fillMaxWidth(),*/verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) // will hold rating stars
                {
                    val maxStars = 5
                    val rating = review.rating ?: 0

                    repeat(rating.coerceAtMost(maxStars)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star (Note)",
                            tint = Color(0xFFFF6E41),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    repeat(maxStars - rating.coerceAtLeast(0).coerceAtMost(maxStars)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star (Non noté)",
                            tint = Color.Gray,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
            Text(text = review.comment_text)

        }
        if (myComment)
        {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Supprimer commentaire",
                tint = Color.Red,
                modifier = Modifier.size(32.dp).clickable
                {
                    onDelete(review.id)
                    Toast.makeText(context, "Commentaire supprimé", Toast.LENGTH_SHORT).show()
                }
            )
        }
        else
        {
            Spacer(modifier = Modifier.width(32.dp))

        }


    }


}

val reviewExample =     Review( 1, "https://t4.ftcdn.net/jpg/03/83/25/83/360_F_383258331_D8imaEMl8Q3lf7EKU2Pi78Cn0R7KkW9o.jpg","John Doe","The dish was absolutely delicious! Fresh ingredients and perfect seasoning. Highly recommend it.", "30 minutes ago",rating = 5)


@Preview(showBackground = true)
@Composable
fun ReviewCardPreview()
{

    //ReviewCard(reviewExample)
}