package com.example.products.repository

import android.util.Log
import com.example.products.model.AuthResponse
import com.example.products.model.Order
import com.example.products.model.OrderState
import com.example.products.model.Person
import com.example.products.model.Product
import com.example.products.model.SignInRequest
import com.example.products.model.User
import com.example.products.utils.retrofit.AUTH_TYPE
import com.example.products.utils.retrofit.ApiResult
import com.example.products.utils.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private val TAG = ApiRepository::class.simpleName

/**
 * РепозиториЙ, подключенный к API
 */
class ApiRepository() : Repository {

    override suspend fun login(user: User): AuthResponse = RetrofitClient.retrofit.login(user)

    override fun signIn(user: User, person: Person): Flow<ApiResult<AuthResponse>> = flow {
        try {
            val response = RetrofitClient.retrofit.signIn(SignInRequest(user, person))
            emit(ApiResult.Success(response))
        } catch (ex: Exception) {
            emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
        }
    }.flowOn(Dispatchers.IO)

    override fun getProductList(token: String): Flow<ApiResult<List<Product>>> = flow {
        try {
            val response = RetrofitClient.retrofit.getProducts(token)
            emit(ApiResult.Success(response))
        } catch (ex: Exception) {
            emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
        }
    }.flowOn(Dispatchers.IO)
        .catch {
            Log.d(TAG, "getModelsList: ${it.message}")
        }

    override fun getProductById(token: String, id: Int): Flow<ApiResult<Product>> = flow {
        try {
            val response = RetrofitClient.retrofit.getProductById(token, id)
            emit(ApiResult.Success(response))
        } catch (ex: Exception) {
            emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
        }
    }.flowOn(Dispatchers.IO)
        .catch {
            Log.e(TAG, "getProductById: ${it.message}")
        }

    override suspend fun checkLogin(login: String): Flow<ApiResult<Boolean>> =
        flow {
            try {
                if (! RetrofitClient.retrofit.checkLogin(login))
                    emit(ApiResult.Success(true))
                else
                    emit(
                        ApiResult.Failure(
                            Exception("Такой логин уже есть в системе"),
                            "Этот логин занят"
                        )
                    )
            } catch (ex: Exception) {
                emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
            }
        }


    override suspend fun getPerson(token: String): Person =
        RetrofitClient.retrofit.getPerson(token)

    override fun getOrders(token: String): Flow<ApiResult<List<Order>>> = flow {
        try {
            val response = RetrofitClient.retrofit.getOrders(AUTH_TYPE + token)
            if (response.isEmpty())
                emit(ApiResult.Empty())
            else
                emit(ApiResult.Success(response))
        } catch (ex: Exception) {
            emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
        }
    }.flowOn(Dispatchers.IO)

    override fun getProductInOrderById(token: String, id: Int): Flow<ApiResult<List<Product>>> =
        flow {
            try {
                val response = RetrofitClient.retrofit.getProductsInOrderById(AUTH_TYPE + token, id)
                if (response.isEmpty())
                    emit(ApiResult.Empty())
                else
                    emit(ApiResult.Success(response))
            } catch (ex: Exception) {
                emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun addToLastOrder(token: String, product: Product): Flow<ApiResult<Order>> =
        flow {
            try {
                val response = RetrofitClient.retrofit.addToLastOrder(AUTH_TYPE + token, product)
                emit(ApiResult.Success(Order()))
            } catch (ex: Exception) {
                emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
            }
        }.flowOn(Dispatchers.IO)

    override fun getOrderStateById(token: String, id: Int): Flow<ApiResult<OrderState>> = flow {
        try {
            val response = RetrofitClient.retrofit.getOrderStateById(AUTH_TYPE + token, id)
            emit(ApiResult.Success(response))
        } catch (ex: Exception) {
            emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
        }
    }.flowOn(Dispatchers.IO)

    override fun getOrderById(token: String, id: Int): Flow<ApiResult<Order>> = flow {
        try {
            val response = RetrofitClient.retrofit.getOrderById(AUTH_TYPE + token, id)
            emit(ApiResult.Success(response))
        } catch (ex: Exception) {
            emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
        }
    }.flowOn(Dispatchers.IO)

    override fun deleteFromLastOrder(token: String, id: Int): Flow<ApiResult<Unit>> = flow {
        try {
            RetrofitClient.retrofit.deleteFromLastOrder(AUTH_TYPE + token, id)
            emit(ApiResult.Success(Unit))
        } catch (ex: Exception) {
            emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
        }
    }.flowOn(Dispatchers.IO)

    override fun updateProductsIOrder(
        token: String,
        productsInOrder: List<Product>,
    ): Flow<ApiResult<Unit>> = flow {
        try {
            RetrofitClient.retrofit.updateProductsInOrder(AUTH_TYPE + token, productsInOrder)
            emit(ApiResult.Success(Unit))
        } catch (ex: Exception) {
            emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
        }
    }.flowOn(Dispatchers.IO)

    override fun closeCart(token: String, orderId: Int, address: String): Flow<ApiResult<Unit>> =
        flow {
            try {
                RetrofitClient.retrofit.closeCart(AUTH_TYPE + token, orderId, address)
                emit(ApiResult.Success(Unit))
            } catch (ex: Exception) {
                emit(ApiResult.Failure(ex, ex.getRussianErrorMessage()))
            }
        }.flowOn(Dispatchers.IO)
}


fun Exception.getRussianErrorMessage(): String {
    return when (this) {
        is HttpException -> {
            when (code()) {
                400 -> {
                    if (message().isNullOrBlank()) {
                        "Неверный запрос"
                    } else {
                        (response()?.errorBody() as ResponseBody).run {
                            var err: String
                            charStream().apply {
                                err = readText()
                                close()
                            }
                            close()
                            err
                        }
                    }
                }

                401 -> "Требуется авторизация"
                403 -> "Доступ запрещен"
                404 -> "Не найдено"
                408 -> "Время ожидания истекло"
                500 -> "Внутренняя ошибка сервера"
                502 -> "Плохой шлюз"
                503 -> "Сервис недоступен"
                504 -> "Шлюз не отвечает"
                else -> "Ошибка ${code()} ${message()}"
            }
        }

        is SocketTimeoutException -> {
            "Время ожидания истекло. " +
                    "Пожалуйста проверьте подключение к интернету"
        }

        is UnknownHostException -> {
            "Невозможно установить соединение. " +
                    "Пожалуйста проверьте подключение к интернету"
        }

        is ConnectionShutdownException -> {
            "Подключение прервано" +
                    "Пожалуйста проверьте подключение к интернету"
        }

        is IOException -> {
            "Сервер не отвечает. Попробуйте позже"
        }

        is IllegalStateException -> {
            "${message}"
        }

        else -> "Неизвестная ошибка"
    }
}