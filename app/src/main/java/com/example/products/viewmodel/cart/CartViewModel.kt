package com.example.products.viewmodel.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.products.model.Order
import com.example.products.repository.Repository
import com.example.products.utils.retrofit.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    val repository: Repository
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _orders = MutableStateFlow(listOf<Order>())
    val currentOrders = _orders.asStateFlow()

    var token = ""

    fun fetchOrders(){
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    ordersApiState = ApiResult.Loading()
                )
            }
            repository.getOrders(token)
                .onEach {apiResult->
                    if(apiResult is ApiResult.Success){
                        apiResult.data.forEach{ order ->
                            repository.getOrderStateById(token, order.stateId)
                                .collect{
                                    if (it is ApiResult.Success){
                                        order.orderState = it.data
                                    }
                                }
                        }
                    }
                }
                .collect{ apiResult->
                    _uiState.update {uiState ->
                        uiState.copy(
                            ordersApiState = apiResult
                        )
                    }
                    if(apiResult is ApiResult.Success){
                        _orders.update {
                            apiResult.data
                        }
                    }

                }
        }
    }


    data class UiState(
        val ordersApiState: ApiResult<List<Order>> = ApiResult.Loading()
    )

    companion object{
        class ViewModelFactory(val repository: Repository): ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(repository) as T
            }
        }
    }
}