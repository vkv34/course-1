package com.example.products.utils.retrofit

sealed class ApiResult<T>() {
    class Loading<T> : ApiResult<T>()
    class Failure<T>(val e: Exception, val msg: String) : ApiResult<T>()
    class Success<T>(val data: T) : ApiResult<T>()
    class Empty<T> : ApiResult<T>()
}
