package com.example.products.viewmodel.cart

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.products.model.Product
import com.example.products.repository.Repository
import com.example.products.utils.retrofit.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.Address
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class CartDetailViewModel(
    val repository: Repository,
    val orderId: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _productsList = MutableStateFlow(listOf<Product>())
    val currentProducts = _productsList.asStateFlow()

    var token = ""

    private fun getOrderTitle() {
        viewModelScope.launch {
            repository.getOrderById(token, orderId)
                .collect { apiResult ->
                    if (apiResult is ApiResult.Success) {
                        _uiState.update {
                            val dateFormat: DateFormat = SimpleDateFormat.getDateInstance(
                                SimpleDateFormat.MEDIUM,
                                Locale.getDefault()
                            )

                            it.copy(
                                title = "Заказ от ${dateFormat.format(apiResult.data.dateCreate)}",
                                isEditable = apiResult.data.stateId == 1,
                                address = apiResult.data.address
                            )


                        }
                    }
                }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            setProductListApiState(ApiResult.Loading())
            delay(500L)
            repository.getProductInOrderById(token, orderId)
                .collect { apiResult ->
                    setProductListApiState(apiResult)
                    if (apiResult is ApiResult.Success) {
                        _productsList.update {
                            apiResult.data
                        }
                        var totalCost = 0.0
                        apiResult.data.forEach { product ->
                            repository.getProductById(token, product.id)
                                .collect { prApiRes ->
                                    if (prApiRes is ApiResult.Success) {
                                        product.maxQuantity = prApiRes.data.quantity

                                        totalCost += product.quantity * product.price
                                    }
                                }
                        }
                        totalCost = (totalCost*100.00).roundToInt() / 100.00
                        _uiState.update {
                            it.copy(
                                totalCost = totalCost
                            )
                        }

                        _productsList.update {
                            apiResult.data
                        }

                    } else {
                        _productsList.update { listOf() }
                    }
                }
        }
        getOrderTitle()
    }

    fun deleteFromOrder(product: Product) {

        viewModelScope.launch {
            repository.deleteFromLastOrder(token, product.id)
                .collect { apiResult ->
                    _uiState.update {
                        it.copy(
                            deleteResult = apiResult
                        )
                    }
                    fetchProducts()
                }
        }
    }

    fun setAddress(address: String){
        if (address.isBlank()){
            _uiState.update {
                it.copy(
                    isAddressError = true,
                    addressError = "Поле обязательно для заполнения",
                    address = address
                )
            }
            return
        }
        else{
            _uiState.update {
                it.copy(
                    isAddressError = false,
                    addressError = "",
                    address = address
                )
            }
        }

    }

    fun closeCart(){
        if(_uiState.value.isAddressError)
            return
        _uiState.update {
            it.copy(
                orderResult = ApiResult.Loading()
            )
        }
        viewModelScope.launch {
            delay(1000)
            repository.closeCart(token, orderId, _uiState.value.address)
                .collect{apiResult->
                    _uiState.update {
                        it.copy(
                            orderResult = apiResult
                        )
                    }
                    if (apiResult is ApiResult.Success)
                        _uiState.update {
                            it.copy(
                                isEditable = false
                            )
                        }
                }
        }
    }

    private val updateTimer = object : CountDownTimer(
        2000L,
        1000L
    ) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            updateOrder()
        }

    }

    fun changeProductQuantity(id: Int, quantity: Int) {

        _productsList.update {
            it.map { product ->
                if (product.id == id) {
                    if (quantity > 0 && quantity <= product.maxQuantity + product.quantity)
                        product.copy(quantity = quantity, updated = true)
                    else if (quantity > product.maxQuantity + product.quantity)
                        product.copy(quantity = product.maxQuantity + product.quantity, updated = true)
                    else
                        product
                } else {
                    product
                }
            }
        }

        updateTimer.cancel()
        updateTimer.start()
    }

    fun updateOrder(){
        viewModelScope.launch {
            _productsList.map {
                it.filter { p -> p.updated }
            }.flowOn(Dispatchers.IO)
                .collect { products ->
                    repository.updateProductsIOrder(token, products)
                        .collect { apiResult ->

                            if (apiResult is ApiResult.Success)

                                _uiState.update {
                                    it.copy(
                                        updateResult = apiResult
                                    )
                                }
                        }
                }
        }
    }


    private fun setProductListApiState(apiState: ApiResult<List<Product>>) {
        _uiState.update {
            it.copy(
                productListApiState = apiState
            )
        }
    }


    data class UiState(
        val productListApiState: ApiResult<List<Product>> = ApiResult.Empty(),
        val title: String = "Заказ",
        val deleteResult: ApiResult<Unit> = ApiResult.Empty(),
        val updateResult: ApiResult<Unit> = ApiResult.Empty(),
        val orderResult: ApiResult<Unit> = ApiResult.Empty(),
        val isEditable: Boolean = false,
        val totalCost: Double = 0.0,
        val address: String = "",
        val isAddressError: Boolean = false,
        val addressError: String = ","
    )


    companion object {
        class Factory(
            private val repository: Repository,
            private val orderId: Int,
        ) : ViewModelProvider.NewInstanceFactory() {

            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                modelClass
                    .getConstructor(Repository::class.java, Int::class.java)
                    .newInstance(repository, orderId)
        }
    }
}