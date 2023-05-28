package com.example.products.model

import com.google.gson.annotations.SerializedName

/**
 * Класс ответа на авторизацию
 *
 * @param token токен авторизации
 * @param user пользоватлеь
 */
data class AuthResponse(
    @SerializedName("access_token") val token: String,
    @SerializedName("user") val user: User
)