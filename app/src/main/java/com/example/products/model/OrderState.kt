package com.example.products.model


/**
 * Состояние заказа
 *
 * @param idState код состояния
 * @param state состояние
 */
data class OrderState(
    val idState: Int = 0,
    val state: String = ""
)