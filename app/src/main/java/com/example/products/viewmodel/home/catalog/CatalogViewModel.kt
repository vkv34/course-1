package com.example.products.viewmodel.home.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.products.model.Product
import com.example.products.repository.Repository
import com.example.products.utils.retrofit.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatalogViewModel(val repository: Repository, private var token: String): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _productList = MutableStateFlow(listOf<Product>())
    val productList = _productList.asStateFlow()

    fun fetchProducts(){
        _uiState.update { it.copy(
            productListState = ApiResult.Loading()
        ) }
        viewModelScope.launch{
            repository.getProductList(token)
                .collect{apiResult->
                    if (apiResult is ApiResult.Success){
                        _productList.update { apiResult.data }
                    }
                    _uiState.update { it.copy(
                        productListState = apiResult
                    ) }
                }
        }
    }

    data class UiState(
        val productListState: ApiResult<List<Product>> = ApiResult.Loading()
    )



    companion object {
        class Factory(val repository: Repository,val token: String) : ViewModelProvider.NewInstanceFactory() {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CatalogViewModel(repository, token) as T
            }
        }
    }
}