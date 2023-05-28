package com.example.products.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

/**
 * Класс информации о пользователе
 *
 * @param userId код пользователя
 * @param name имя
 * @param secondName фамилия
 * @param middleName отчество
 * @param email почта
 * @param birthday дата рождения
 */
data class Person(
    @SerializedName("UserId") val userId: Int = 0,
    @SerializedName("Name") val name: String = "",
    @SerializedName("SecondName") val secondName: String = "",
    @SerializedName("MiddleName") val middleName: String = "",
    @SerializedName("Email") val email: String = "",
    @SerializedName("Birthday") val birthday: String? = null,
)