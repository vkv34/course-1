package com.example.products

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.products.ui.screen.detail.DetailsScreen
import com.example.products.ui.theme.ProductsTheme

class DetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val id = intent.extras?.getInt("id") ?: 0
                    val quantity = intent.extras?.getInt("quantity")?:0

                    DetailsScreen(productId = id, quantity = quantity)

                }
            }
        }
    }
}