package com.example.products.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.util.Date


/**
 * Класс заказа
 *
 * @param idOrder код заказа
 * @param dateCreate дата заказа
 * @param cost стоимоть заказа
 * @param userId код пользователя
 * @param stateId код состояния
 * @param dateFinish дата доставки
 * @param address адресс доставки
 * @param orderState состояние заказа
 */
data class Order(
    val idOrder: Int = 0,
    val dateCreate: Date = Date(),
    val cost: Double = Double.NaN,
    val userId: Int = 0,
    val stateId: Int = 0,
    val dateFinish: Date = Date(),

    @SerializedName("address")
    val address: String = "",
    var orderState: OrderState = OrderState()
)