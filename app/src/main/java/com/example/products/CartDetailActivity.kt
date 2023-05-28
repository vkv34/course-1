package com.example.products

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.products.ui.screen.cart.details.CartDetailsScreen
import com.example.products.ui.theme.ProductsTheme

class CartDetailActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.extras?.getInt("id", 0)?:0


        setContent {
            ProductsTheme {
                CartDetailsScreen(id)
            }
        }
    }
}