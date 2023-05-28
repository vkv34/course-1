package com.example.products.viewmodel.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.products.model.Order
import com.example.products.model.Product
import com.example.products.repository.Repository
import com.example.products.utils.retrofit.ApiResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: Repository,
    private val productId: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _product = MutableStateFlow(Product())
    val currentProduct = _product.asStateFlow()

    var token: String = ""

    fun fetchProduct() {
        _uiState.update { uiState ->
            uiState.copy(
                apiResult = ApiResult.Loading()
            )
        }
        viewModelScope.launch {
            repository.getProductById(token, productId)
                .collect { apiResult ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            apiResult = apiResult
                        )
                    }
                    if (apiResult is ApiResult.Success) {
                        _product.update { apiResult.data }
                    }
                }
        }
    }

    fun changeQuantity(quantity: Int) {
        if (quantity < 0)
            return

        if (quantity > _product.value.quantity) {
            _uiState.update {
                it.copy(
                    currentQuantity = _product.value.quantity
                )
            }
            return
        }
        _uiState.update {
            it.copy(
                currentQuantity = quantity
            )
        }
    }


    fun addTolastOrder() {
        _uiState.update {
            it.copy(
                addResult = ApiResult.Loading()
            )
        }
        viewModelScope.launch {
            delay(500L)
            repository.addToLastOrder(
                token,
                product = _product.value.copy(quantity = _uiState.value.currentQuantity)
            )
                .collect { apiResult ->
                    _uiState.update {
                        it.copy(
                            addResult = apiResult
                        )
                    }
                }

        }
    }

    data class UiState(
        val apiResult: ApiResult<Product> = ApiResult.Loading(),
        val currentQuantity: Int = 0,
        val addResult: ApiResult<Order> = ApiResult.Empty(),
    )


    companion object {
        class Factory(private val repository: Repository, private val productId: Int) :
            ViewModelProvider.NewInstanceFactory() {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DetailViewModel(repository, productId) as T
            }
        }
    }

}