package com.example.products.ui.screen.detail

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.products.model.Order
import com.example.products.model.Product
import com.example.products.repository.ApiRepository
import com.example.products.ui.controls.QuantityPicker
import com.example.products.ui.screen.ErrorScreen
import com.example.products.utils.account.getToken
import com.example.products.utils.retrofit.ApiResult
import com.example.products.utils.retrofit.BASE_URL
import com.example.products.viewmodel.detail.DetailViewModel
import kotlinx.coroutines.launch

/**
 * Экран деталей о товаре
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    productId: Int,
    quantity: Int
) {
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Companion.Factory(
            repository = ApiRepository(),
            productId = productId
        )
    )

    val uiState by viewModel.uiState.collectAsState()
    val product by viewModel.currentProduct.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        val token = getToken()

        viewModel.token = token

        viewModel.fetchProduct()

    }

    LaunchedEffect(uiState.apiResult){
        when(uiState.apiResult){
            is ApiResult.Success ->
                viewModel.changeQuantity(quantity)
            else -> Unit
        }
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.addResult){
        when(uiState.addResult){
            is ApiResult.Failure -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar((uiState.addResult as ApiResult.Failure<Order>).msg)
                }
            }
            is ApiResult.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Ваш товар в корзине")
                }
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = { TopBar() },
        snackbarHost = { SnackbarHost (snackbarHostState)},
        bottomBar = {
            BottomBar(
                quantity = uiState.currentQuantity,
                isLoading = uiState.addResult is ApiResult.Loading,
                onQuantityChanged = {
                    viewModel.changeQuantity(it)
                },
                addToCartClick = {
                    viewModel.addTolastOrder()
                }
            )
        }
    ) { pv ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
        ) {
            when (uiState.apiResult) {
                is ApiResult.Loading -> {
                    CircularProgressIndicator()
                }

                is ApiResult.Empty -> {
                    Text(text = "Продукт уже купили")
                }

                is ApiResult.Failure -> {
                    ErrorScreen(error = (uiState.apiResult as ApiResult.Failure<Product>).msg)
                }

                is ApiResult.Success -> {
                    ProductCard(product = product)
                }
            }
        }
    }

}

/**
 * Верхнее меню
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String = "Продукт") {
    val context = LocalContext.current
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            /*IconButton(onClick = { *//*TODO*//* }) {
                Icon(
                    imageVector = Icons.TwoTone.ShoppingCart,
                    contentDescription = Icons.TwoTone.ShoppingCart.name,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }*/
        },
        navigationIcon = {
            IconButton(onClick = {
                if (context is Activity) {
                    context.finish()
                }
            }) {
                Icon(
                    imageVector = Icons.TwoTone.ArrowBack,
                    contentDescription = Icons.TwoTone.ArrowBack.name
                )
            }
        }
    )
}

/**
 * Карточка продукта
 */
@Composable
fun ProductCard(product: Product) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(BASE_URL + product.photo)
                    .crossfade(true)
//                    .size(Dimension(300), Dimension(300))
                    .build(),
                contentDescription = product.name
            )
            Text(text = product.name)
            Text(text = product.description)
            Text(text = product.name)
            Text(text = "Цена: ${product.price - (product.price * product.discountRate)}")
        }
    }
}

/**
 * Нижнее меню
 */
@Composable
fun BottomBar(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    addToCartClick: () -> Unit,
    isLoading:Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        QuantityPicker(
            quantity = quantity,
            onQuantityChanged = onQuantityChanged
        )
        Button(
            enabled = !isLoading,
            onClick = { addToCartClick.invoke() }
        ) {
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "В корзину")
        }
    }
}