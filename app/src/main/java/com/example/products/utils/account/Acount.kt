package com.example.products.utils.account

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import io.paperdb.Paper
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

private const val tokenKey = "token"
private const val BOOK = "Account"

suspend fun login(token: String) {
    withContext(coroutineContext) {
        Paper.book(BOOK).write(tokenKey, token)
    }
}

suspend fun logout() {
    withContext(coroutineContext) {
        Paper.book(BOOK).delete(tokenKey)
        Log.d("Account", "log out")
    }
}

fun isLogin(): Boolean =
    !Paper.book(BOOK).read<String>(tokenKey).isNullOrEmpty()

suspend fun getToken(): String =
    withContext(coroutineContext){
        Paper.book(BOOK).read<String>(tokenKey)?:""
    }