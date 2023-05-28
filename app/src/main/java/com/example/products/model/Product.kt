package com.example.products.model

import com.google.gson.annotations.SerializedName


/**
 * Класс продукта
 *
 * @param id код продукта
 * @param name наименование продукта
 * @param price стоимость
 * @param discountRate скидка
 * @param specifications характеристки
 * @param description описание
 * @param photo фото
 * @param quantity количество
 * @param brandId код бренда
 * @param colorId код цвета
 * @param productStateId код состояния
 * @param categoryId код категории
 * @param updated флаг изменения
 * @param maxQuantity максимальное количество
 */
data class Product(
    var id: Int = 0,
    var name: String = "",
    var price: Double = 0.0,
    var discountRate: Double = 1.0,
    var specifications: String = "",
    var description: String = "",
    var photo: String? = "",
    var quantity: Int = 0,
    var brandId: Int = 0,
    var colorId: Int = 0,
    var productStateId: Int = 1,
    var categoryId: Int = 0,

    var updated: Boolean = false,
    var maxQuantity: Int = 0
)