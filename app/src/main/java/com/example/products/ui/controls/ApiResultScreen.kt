package com.example.products.ui.controls

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import com.example.products.ui.screen.ErrorScreen
import com.example.products.utils.retrofit.ApiResult

/**
 * Экран результата api
 *
 * @param loading экран загрузки
 * @param empty пустой экран
 * @param failure экран ошибки
 * @param success экран успеха
 *
 */
@Composable
fun <T> ApiResult<T>.ApiResultScreen(
    loading: (@Composable (ApiResult.Loading<T>) -> Unit)? = null,
    empty: (@Composable (ApiResult.Empty<T>) -> Unit)? = null,
    failure: (@Composable (ApiResult.Failure<T>) -> Unit)? = null,
    success: (@Composable (ApiResult.Success<T>) -> Unit)? = null,
) {

    when (this) {
        is ApiResult.Empty -> if (empty != null) {
            empty.invoke(this)
        } else {
            ErrorScreen(error = "Тут ничего нет")
        }

        is ApiResult.Failure -> if (failure != null) {
            failure.invoke(this)
        } else {
            ErrorScreen(error = this.msg)
        }

        is ApiResult.Loading -> if (loading != null) {
            loading.invoke(this)
        } else {
            CircularProgressIndicator()
        }

        is ApiResult.Success -> success?.invoke(this)
    }

}