package com.example.products.ui.screen.cart.details

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Dimension
import com.example.products.DetailActivity
import com.example.products.model.Product
import com.example.products.repository.ApiRepository
import com.example.products.ui.controls.ApiResultScreen
import com.example.products.ui.controls.OutlineTextFieldWithValidation
import com.example.products.ui.controls.QuantityPicker
import com.example.products.utils.account.getToken
import com.example.products.utils.retrofit.ApiResult
import com.example.products.utils.retrofit.BASE_URL
import com.example.products.viewmodel.cart.CartDetailViewModel
import kotlinx.coroutines.launch

/**
 *
 * Экран деталей корзины
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun CartDetailsScreen(
    orderId: Int,
) {

    val viewModel: CartDetailViewModel = viewModel(
        factory = CartDetailViewModel.Companion.Factory(
            ApiRepository(),
            orderId
        )
    )

    val uiState by viewModel.uiState.collectAsState()
    val productList by viewModel.currentProducts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.token = getToken()
        viewModel.fetchProducts()
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.deleteResult) {
        when (uiState.deleteResult) {
            is ApiResult.Failure ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar((uiState.deleteResult as ApiResult.Failure<Unit>).msg)
                }

            is ApiResult.Success ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Успешно удалено")
                }

            else -> Unit
        }
    }
    LaunchedEffect(uiState.updateResult) {
        when (uiState.updateResult) {
            is ApiResult.Failure ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar((uiState.deleteResult as ApiResult.Failure<Unit>).msg)
                }

            is ApiResult.Success ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Успешно изменено")
                }

            else -> Unit
        }
    }

    fun refresh() {
        viewModel.fetchProducts()
    }

    val pullRefreshState =
        rememberPullRefreshState(uiState.productListApiState is ApiResult.Loading, ::refresh)

    val scaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(uiState.orderResult) {
        when (uiState.orderResult) {
            is ApiResult.Failure -> {
                coroutineScope.launch {
                    //scaffoldState.bottomSheetState.collapse()
                    snackbarHostState.showSnackbar((uiState.orderResult as ApiResult.Failure<Unit>).msg)
                }
            }

            is ApiResult.Loading -> scaffoldState.bottomSheetState.expand()
            is ApiResult.Success -> {
                coroutineScope.launch {
                    scaffoldState.bottomSheetState.collapse()
                    snackbarHostState.showSnackbar("Заказ успешно оформлен")
                }
            }

            else -> Unit
        }
    }

    val keyboard = LocalSoftwareKeyboardController.current

    BottomSheetScaffold(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        topBar = { TopBar(uiState.title) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        scaffoldState = scaffoldState,
        sheetShape = MaterialTheme.shapes.medium,
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        backgroundColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    BottomBar(
                        isEditable = uiState.isEditable,
                        isCollapsed = scaffoldState.bottomSheetState.isCollapsed,
                        text = "Итог: ${uiState.totalCost}",
                        onClick = {
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.expand()

                            }
                        },
                        onCloseClick = {
                            coroutineScope.launch {
                                keyboard?.hide()
                                scaffoldState.bottomSheetState.collapse()
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlineTextFieldWithValidation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp),
                    value = uiState.address,
                    onValueChange = { viewModel.setAddress(it) },
                    enabled = uiState.orderResult !is ApiResult.Loading,
                    label = {
                        Text(text = "Адрес доставки")
                    },
                    isError = uiState.isAddressError,
                    errorMsg = uiState.addressError,
                )
                if (uiState.isEditable){
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 8.dp),
                        enabled = uiState.orderResult !is ApiResult.Loading,
                        onClick = {
                            viewModel.closeCart()
                        }) {
                        AnimatedVisibility(visible = uiState.orderResult is ApiResult.Loading) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(text = "Оформить заказ")
                    }
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val scope = rememberCoroutineScope()

                val context = LocalContext.current

                uiState.productListApiState.ApiResultScreen(
                    loading = {}
                ) {

                }

                ProductList(
                    modifier = Modifier
                        .pullRefresh(pullRefreshState)
                        .background(color = MaterialTheme.colorScheme.background),
                    pullRefreshState = pullRefreshState,
                    products = productList,
                    isEditable = uiState.isEditable,
                    onClick = {
                        scope.launch {
                            context.startActivity(
                                Intent(
                                    context,
                                    DetailActivity::class.java
                                ).apply {
                                    putExtra("id", it.id)
                                    putExtra("quantity", it.quantity)
                                }
                            )
                        }
                    },
                    onDelete = { viewModel.deleteFromOrder(it) },
                    onQuantityChanged = { product, quantity ->
                        viewModel.changeProductQuantity(product.id, quantity)
//                        viewModel.updateOrder()
                    },
                    refreshing = uiState.productListApiState is ApiResult.Loading

                )
            }


        }
    }

}

/**
 * Нижнее меню
 */
@Composable
fun BottomBar(
    isEditable: Boolean,
    isCollapsed: Boolean,
    text: String,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditable) {
                Text(text = text)
                if (isCollapsed)
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(text = "Заказать")
                    }
                else
                    Button(
                        onClick = onCloseClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(text = "Закрыть")
                    }
                /* Column {
                     AnimatedVisibility(visible = isCollapsed) {

                     }
                     AnimatedVisibility(visible = ! isCollapsed) {

                     }
                 }*/
            } else {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/**
 * Верхнее меню
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String = "Заказ") {
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
 *
 * Список продуктов
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductList(
    modifier: Modifier = Modifier,
    products: List<Product>,
    isEditable: Boolean = false,
    onClick: ((Product) -> Unit)? = null,
    onQuantityChanged: (Product, Int) -> Unit,
    onDelete: (Product) -> Unit,
    pullRefreshState: PullRefreshState,
    refreshing: Boolean = false,
) {
    Box(
        modifier = modifier.fillMaxSize(),

        ) {
        LazyColumn {
            items(products) {
                ProductCard(
                    product = it,
                    isEditable = isEditable,
                    onClick = {
                        onClick?.invoke(it)
                    },
                    onQuantityChanged = onQuantityChanged,
                    onDelete = onDelete
                )
                Spacer(modifier = Modifier.height(8.dp))

            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(
                Alignment.TopCenter
            )
        )
    }
}

/**
 * Карта продукта
 */
@Composable
fun ProductCard(
    product: Product,
    isEditable: Boolean = false,
    onClick: (Product) -> Unit,
    onQuantityChanged: (Product, Int) -> Unit,
    onDelete: (Product) -> Unit,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 0.dp, 8.dp, 8.dp)
            .clickable { onClick(product) },
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                val imageModel = ImageRequest.Builder(context)
                    .data(BASE_URL + product.photo)
                    .crossfade(true)
                    .size(Dimension(300), Dimension(300))
                    .build()
                val painter = rememberAsyncImagePainter(model = imageModel)
                Image(
                    painter = painter,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.name)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                QuantityPicker(
                    quantity = product.quantity,
                    isEditable = isEditable,
                    onQuantityChanged = { onQuantityChanged(product, it) })
                Text(text = "Итог: ${(product.price * product.quantity)}")

            }
            Column {
            }
            Column {
                if (isEditable) {
                    IconButton(onClick = { onDelete(product) }) {
                        Icon(
                            imageVector = Icons.TwoTone.Delete,
                            contentDescription = Icons.TwoTone.Delete.name
                        )
                    }
                }
            }

        }
    }
}
