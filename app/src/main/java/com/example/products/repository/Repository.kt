package com.example.products.repository

import com.example.products.model.AuthResponse
import com.example.products.model.Order
import com.example.products.model.OrderState
import com.example.products.model.Person
import com.example.products.model.Product
import com.example.products.model.User
import com.example.products.utils.retrofit.ApiResult
import kotlinx.coroutines.flow.Flow


/**
 * Интерфейс, который описывает все функции, необходимые для работы с репозиторием
 */

interface Repository {

    /**
     * Функция, позволяющая выполинть авторизацию
     * @param user - даные пользователя
     * @return возвращает ответ сервера с данными авторизации
     * @see AuthResponse
     */
    suspend fun login(user: User): AuthResponse

    /**
     * Функция Регситрации
     *
     * @param user пользователь
     * @param person информация о пользователе
     */
    fun signIn(user: User, person: Person): Flow<ApiResult<AuthResponse>>

    /**
     * Функция получения списка продуктов
     *
     * @param token токен авторизациии
     */
    fun getProductList(token: String): Flow<ApiResult<List<Product>>>

    /**
     *
     * Функция получения продукта по его коду
     *
     * @param token токен авторизации
     * @param id код продукта
     */
    fun getProductById(token: String, id: Int): Flow<ApiResult<Product>>


    /**
     *
     * Функция проверки логина
     *
     * @param login логин
     */
    suspend fun checkLogin(login: String): Flow<ApiResult<Boolean>>


    /**
     *
     * Функция получения информации о пользователе
     *
     * @param token токен авторизации
     */
    suspend fun getPerson(token: String): Person

    /**
     *
     * Функция получения списка заказов
     *
     * @param token токен авторизации
     */
    fun getOrders(token: String): Flow<ApiResult<List<Order>>>


    /**
     *
     * ПОлучение продукта в заказе по его коду
     *
     * @param token токен авторизация
     * @param id код продукта в заказе
     */
    fun getProductInOrderById(token: String, id: Int): Flow<ApiResult<List<Product>>>


    /**
     *
     * Добавление продукта в последний заказ
     *
     * @param token токен авторизации
     * @param product продукт
     */
    suspend fun addToLastOrder(token: String, product: Product): Flow<ApiResult<Order>>


    /**
     *
     * Получение статуса заказа
     *
     * @param token токен авторизации
     * @param id код состояния заказа
     */
    fun getOrderStateById(token: String, id: Int): Flow<ApiResult<OrderState>>


    /**
     *
     * Получение заказа по коду
     *
     * @param token токен авторизации
     * @param id код заказа
     */
    fun getOrderById(token: String, id: Int): Flow<ApiResult<Order>>

    /**
     * Удаляет определенный продукт из последнего редактируемого заказа
     * @param token - токен авторизации
     * @param id - Идентификационный номер продукта
     * @return поток с ответом от Api
     * @see ApiResult
     */
    fun deleteFromLastOrder(token: String, id: Int): Flow<ApiResult<Unit>>

    /**
     *
     * Обновление продуктов в заказе
     *
     * @param token токен авторизации
     * @param productsInOrder продукты в заказе
     */
    fun updateProductsIOrder(token: String, productsInOrder: List<Product>): Flow<ApiResult<Unit>>

    /**
     *
     * Оформление заказа
     *
     * @param token токен авторизации
     * @param orderId код заказа
     * @param address адресс заказа
     */
    fun closeCart(token: String, orderId: Int, address: String): Flow<ApiResult<Unit>>
}