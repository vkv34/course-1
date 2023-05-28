package com.example.products.ui.screen.home.catalog

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Dimension
import com.example.products.DetailActivity
import com.example.products.model.Product
import com.example.products.repository.ApiRepository
import com.example.products.utils.retrofit.BASE_URL
import com.example.products.viewmodel.home.catalog.CatalogViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavHostController,
    pv: PaddingValues,
) {
    val viewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModel.Companion.Factory(ApiRepository(), "")
    )

    val coroutineScope = rememberCoroutineScope()

    val products by viewModel.productList.collectAsState()

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(pv)
    ) {
        LaunchedEffect(Unit) {
            viewModel.fetchProducts()
        }
        var search by remember {
            mutableStateOf("")
        }
        /*OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = search,
            onValueChange = { search = it },
            label = {
                Text(text = "Поиск")
            }
        )*/

        /*val sortedProducts by remember {
            mutableStateOf(
                if (search.isBlank()) {
                products
            } else {
                products.filter { product ->
                    (product.name + product.description).lowercase()
                        .contains(search.lowercase())
                }
            })
        }*/

        ProductList(products = products/*, search = search*/) {
            scope.launch {
                val productId = it.id

                context.startActivity(
                    Intent(
                        context,
                        DetailActivity::class.java
                    ).apply {
                        putExtra("id", productId)
                    }
                )
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductList(
    products: List<Product>,  /*search: String,*/
    onClick: ((Product) -> Unit)? = null,
) {
    var search by remember {
        mutableStateOf("")
    }
    LazyColumn {

        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = search,
                onValueChange = { search = it },
                label = {
                    Text(text = "Поиск")
                }
            )
        }
        items(products) { product ->
            if (search.isEmpty()) {
                ProductCard(product = product,
                    onClick = {
                        onClick?.invoke(it)
                    })
                Spacer(modifier = Modifier.height(8.dp))
            } else if ((product.name + product.description).lowercase()
                    .contains(search.lowercase())
            ) {
                ProductCard(product = product,
                    onClick = {
                        onClick?.invoke(it)
                    })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: (Product) -> Unit) {
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
                Text(text = product.description)
            }
            Column(

            ) {
                Text(text = product.price.toString())
            }

        }
    }
}