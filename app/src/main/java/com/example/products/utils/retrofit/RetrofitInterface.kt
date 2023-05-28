package com.example.products.utils.retrofit


import com.example.products.model.*
import retrofit2.http.*

interface RetrofitInterface {

    @GET(PRODUCTS_PATH)
    suspend fun getProducts(@Header(AUTH_HEADER) authorization: String): List<Product>

    @GET("$PRODUCTS_PATH/{id}")
    suspend fun getProductById(
        @Header(AUTH_HEADER) authorization: String,
        @Path("id") id: Int,
    ): Product

    @GET("$PRODUCTS_PATH/model/{id}")
    suspend fun getProductByModelId(
        @Header(AUTH_HEADER) authorization: String,
        @Path("id") id: Int,
    ): Product

    @POST(LOGIN_PATH)
    suspend fun login(@Body user: User): AuthResponse

    @POST(SIGN_IN_PATH)
    suspend fun signIn(@Body signInRequest: SignInRequest): AuthResponse

    @GET("$CHECK_LOGIN_PATH/{login}")
    suspend fun checkLogin(@Path("login") login: String): Boolean

    @GET(GET_PERSON_PATH)
    suspend fun getPerson(@Header(AUTH_HEADER) authorization: String): Person

    @GET(CART_PATH)
    suspend fun getOrders(@Header(AUTH_HEADER) authorization: String): List<Order>

    @GET("$CART_PATH/{id}")
    suspend fun getProductsInOrderById(@Header(AUTH_HEADER) authorization: String, @Path("id") id: Int): List<Product>

    @POST(CART_PATH)
    suspend fun addToLastOrder(@Header(AUTH_HEADER) authorization: String,@Body product: Product)

    @GET("$ORDER_STATE_PATH/{id}")
    suspend fun getOrderStateById(@Header(AUTH_HEADER) authorization: String,@Path("id") id: Int): OrderState

    @GET("$ORDERS_PATH/{id}")
    suspend fun getOrderById(@Header(AUTH_HEADER) authorization: String,@Path("id") id: Int): Order

    @DELETE("$CART_PATH/{id}")
    suspend fun deleteFromLastOrder(@Header(AUTH_HEADER) authorization: String,@Path("id") id: Int)

    @PUT(CART_PATH)
    suspend fun updateProductsInOrder(@Header(AUTH_HEADER) authorization: String,@Body products: List<Product>)

    @POST("$CART_PATH/close/{id}")
    suspend fun closeCart(@Header(AUTH_HEADER) authorization: String, @Path("id") orderId: Int, @Body address: String)
}