package com.example.products.model


/**
 * КЛасс запроса на регистрацию
 *
 * @param user пользователь
 * @param person информауия о пользователе
 */
data class SignInRequest (
    val user: User = User(),
    val person: Person = Person()
)