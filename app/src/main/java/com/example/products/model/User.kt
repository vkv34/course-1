package com.example.products.model

import com.google.gson.annotations.SerializedName

/**
 * Класс пользователя
 *
 * @param id код пользователя
 * @param login логин
 * @param password пароль
 * @param roleId код роли
 */
data class User(
    @SerializedName("Id") val id: Int = 0,
    @SerializedName("Login") val login: String = "",
    @SerializedName("Password") val password: String = "",
    @SerializedName("RoleId") val roleId: Int = 0
)