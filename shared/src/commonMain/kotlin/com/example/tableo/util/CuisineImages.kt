package com.example.tableo.util

import com.example.tableo.resources.Res
import com.example.tableo.resources.cuisine_asiatisch
import com.example.tableo.resources.cuisine_burger
import com.example.tableo.resources.cuisine_fastfood
import com.example.tableo.resources.cuisine_fruehstueck
import com.example.tableo.resources.cuisine_getraenke
import com.example.tableo.resources.cuisine_indisch
import com.example.tableo.resources.cuisine_kaffee
import com.example.tableo.resources.cuisine_pasta
import com.example.tableo.resources.cuisine_pizza
import com.example.tableo.resources.cuisine_shisha
import com.example.tableo.resources.cuisine_sushi
import org.jetbrains.compose.resources.DrawableResource

/**
 * Passendes Bild zur Küche aus Firestore, z.B. "Burger" oder "Frühstück".
 * Groß-/Kleinschreibung und Leerzeichen sind egal, unbekannte Küchen bekommen null.
 */
fun cuisineImage(cuisine: String): DrawableResource? =
    when (cuisine.trim().lowercase().replace(" ", "").replace("-", "")) {
        "burger" -> Res.drawable.cuisine_burger
        "fastfood" -> Res.drawable.cuisine_fastfood
        "kaffee", "café", "cafe", "coffee" -> Res.drawable.cuisine_kaffee
        "frühstück", "fruehstueck", "brunch" -> Res.drawable.cuisine_fruehstueck
        "shisha" -> Res.drawable.cuisine_shisha
        "indisch" -> Res.drawable.cuisine_indisch
        "getränke", "getraenke", "drinks" -> Res.drawable.cuisine_getraenke
        "pizza" -> Res.drawable.cuisine_pizza
        "pasta" -> Res.drawable.cuisine_pasta
        "sushi" -> Res.drawable.cuisine_sushi
        "asiatisch" -> Res.drawable.cuisine_asiatisch
        else -> null
    }
