package com.example.products.ui.screen.cart

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.products.CartDetailActivity
import com.example.products.model.Order
import com.example.products.repository.ApiRepository
import com.example.products.ui.screen.ErrorScreen
import com.example.products.utils.account.getToken
import com.example.products.utils.retrofit.ApiResult
import com.example.products.viewmodel.cart.CartDetailViewModel
import com.example.products.viewmodel.cart.CartViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 *
 * Экран корзины
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CartScreen() {

    val viewModel: CartViewModel = viewModel(
        factory = CartViewModel.Companion.ViewModelFactory(
            ApiRepository()
        )
    )
    val uiState by viewModel.uiState.collectAsState()


    fun refresh() {
        viewModel.fetchOrders()
    }

    val state = rememberPullRefreshState(uiState.ordersApiState is ApiResult.Loading, ::refresh)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopBar()
        }
    ) { pv ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .pullRefresh(state)
        ) {
            LaunchedEffect(Unit) {
                viewModel.token = getToken()
                viewModel.fetchOrders()
            }

            val orders by viewModel.currentOrders.collectAsState()

            when (uiState.ordersApiState) {
                is ApiResult.Empty -> {
                    ErrorScreen(error = "Заказов еще нет")
                }
                is ApiResult.Failure ->
                    ErrorScreen(
                        error = (uiState.ordersApiState as ApiResult.Failure<List<Order>>).msg
                    )

                is ApiResult.Loading -> {}

                is ApiResult.Success ->
                    OrderList(orders = orders){
                        if (context is Activity){
                            context.startActivity(
                                Intent(
                                    context,
                                    CartDetailActivity::class.java
                                ).apply {
                                    putExtra("id", it.idOrder)
                                    if (it.stateId == 1) {
                                        putExtra("isEditable", true)
                                    }
                                }
                            )
                        }
                    }
            }

            PullRefreshIndicator(
                uiState.ordersApiState is ApiResult.Loading,
                state,
                Modifier.align(Alignment.TopCenter)
            )

        }
    }
}

/**
 * Верхнее меню
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val context = LocalContext.current
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        title = { Text(text = "Корзина") },
        navigationIcon = {
            IconButton(
                onClick = {
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
 * Список заказов
 */
@Composable
private fun OrderList(
    orders: List<Order>,
    onClick: ((Order) -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 0.dp)
    ) {
        LazyColumn {
            items(orders) { order ->
                OrderCard(order = order){
                    onClick?.invoke(it)
                }
            }
        }
    }
}

/**
 * Карточка заказа
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderCard(order: Order, onClick: ((Order)->Unit)? = null) {
    Card(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 12.dp)
            .fillMaxWidth()
            .clickable {
                onClick?.invoke(order)
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            6.dp, 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                val dateFormat: DateFormat = SimpleDateFormat.getDateInstance(
                    SimpleDateFormat.MEDIUM,
                    Locale.getDefault()
                )

                Text(text = dateFormat.format(order.dateCreate))
                Text(text = order.orderState.state)

            }
            Column {
//                Text(text = "Итог: ${order.cost}")
            }
        }
    }
}