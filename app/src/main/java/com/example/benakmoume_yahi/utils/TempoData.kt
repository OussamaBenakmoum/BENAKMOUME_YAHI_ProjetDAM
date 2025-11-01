package com.example.benakmoume_yahi.utils

import com.example.benakmoume_yahi.R
import com.example.benakmoume_yahi.models.Ingredient
import com.example.benakmoume_yahi.models.Restaurant
import com.example.benakmoume_yahi.models.RestaurantCardData
import com.example.benakmoume_yahi.models.Review

val reviews = listOf(
    Review(1,"https://randomuser.me/api/portraits/women/68.jpg","Emma Johnson","The recipe was easy to follow and the dish turned out delicious. Loved the combination of spices!","30 minutes ago",rating = 5),

    Review(2,"https://randomuser.me/api/portraits/men/42.jpg", "Liam Carter","Ingredients were clearly listed, and the preparation steps were simple. My family enjoyed it a lot.","1 hour ago", rating = 5),

    Review(3,"https://randomuser.me/api/portraits/women/12.jpg","Sophia Lee","I appreciated the detailed instructions. Cooking time was perfect, and the flavors were balanced.", "2 hours ago", rating = 5),

    Review(4, "https://randomuser.me/api/portraits/men/75.jpg", "Noah Smith", "The recipe is great, but I found it slightly too spicy. Easy to adjust ingredients for personal taste.", "5 hours ago", rating = 4),

    Review(5,"https://randomuser.me/api/portraits/women/33.jpg","Olivia Martin","Clear step-by-step guide and the dish turned out amazing. Will definitely cook it again.","2 days ago",rating = 5),

    Review(6,"https://randomuser.me/api/portraits/men/29.jpg","Ethan Brown","Very informative recipe. I learned some new techniques while preparing it. Loved it!","3 days ago",rating = 5),

    Review(7,"https://randomuser.me/api/portraits/women/56.jpg","Ava Davis","Instructions are straightforward and ingredients are easy to find. End result was delicious.","4 days ago",rating = 5),

    Review(8,"https://randomuser.me/api/portraits/men/61.jpg","Lucas Wilson","Good recipe, though I had to tweak the cooking time. Flavors were still great.","1 week ago",rating = 4),

    Review(9,"https://randomuser.me/api/portraits/women/24.jpg","Mia Anderson","Loved the recipe! The preparation guide is clear and ingredients are easy to follow.", "1 week ago",rating = 5)
)
val ingredients = listOf(
    Ingredient( "https://www.themealdb.com/images/ingredients/Tomatoes-small.png", "4", "Tomatoes"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Olive%20Oil-small.png", "2 tbs", "Olive Oil"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Onion-small.png", "1 Diced", "Onion"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Red%20Pepper-small.png", "1 sliced", "Red Pepper"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Green%20Pepper-small.png", "1 sliced", "Green Pepper"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Garlic-small.png", "3 Cloves Crushed", "Garlic"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Cumin-small.png", "1 tsp", "Cumin"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Paprika-small.png", "1 tsp", "Paprika"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Salt-small.png", "3/4 teaspoon", "Salt"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Chili%20Powder-small.png", "1/2 teaspoon", "Chili Powder"),
    Ingredient( "https://www.themealdb.com/images/ingredients/Eggs-small.png", "4", "Eggs"),
)


//val restaurantList = listOf(
//    Restaurant(
//        id = 1,
//        name = "Culina Hortus",
//        tags = listOf("gastronomique", "végétarien", "fait maison"),
//        imageRes = R.drawable.restaurant1,
//        address = "38 rue de l'Arbre sec, 69001 Lyon",
//        latitude = 45.767,
//        longitude = 4.834
//    ),
//    Restaurant(
//        id = 2,
//        name = "L'Équilibriste",
//        tags = listOf("végétarien", "éco-responsable", "vegan option"),
//        imageRes = R.drawable.restaurant2,
//        address = "125 avenue Lacassagne, 69003 Lyon",
//        latitude = 45.770,
//        longitude = 4.860
//    ),
//    Restaurant(
//        id = 3,
//        name = "Les Mauvaises Herbes",
//        tags = listOf("végétarien", "bistronomie", "créatif"),
//        imageRes = R.drawable.restaurant3,
//        address = "3 rue du Jardin des Plantes, 69001 Lyon",
//        latitude = 45.765,
//        longitude = 4.835
//    ),
//    Restaurant(
//        id = 4,
//        name = "Équilibres Café",
//        tags = listOf("végétarien", "brunch", "fait maison"),
//        imageRes = R.drawable.restaurant1,
//        address = "4 rue Terme, 69001 Lyon",
//        latitude = 45.766,
//        longitude = 4.834
//    ),
//    Restaurant(
//        id = 5,
//        name = "Like An Elephant",
//        tags = listOf("végan", "bistronomie", "créatif"),
//        imageRes = R.drawable.restaurant2,
//        address = "9 rue Imbert Colomès, 69001 Lyon",
//        latitude = 45.772,
//        longitude = 4.830
//    ),
//    Restaurant(
//        id = 6,
//        name = "Laska",
//        tags = listOf("végétarien", "bio", "gastronomique"),
//        imageRes = R.drawable.restaurant3,
//        address = "13 rue Terraille, 69001 Lyon",
//        latitude = 45.763,
//        longitude = 4.831
//    ),
//    Restaurant(
//        id = 7,
//        name = "Maison Moly",
//        tags = listOf("bistronomie", "fait maison", "produits frais"),
//        imageRes = R.drawable.restaurant1,
//        address = "1 rue Tupin, 69002 Lyon",
//        latitude = 45.757,
//        longitude = 4.827
//    )
//)
