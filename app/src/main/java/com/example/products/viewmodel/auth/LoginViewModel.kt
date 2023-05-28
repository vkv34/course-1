package com.example.products.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.products.repository.Repository
import com.example.products.model.AuthResponse
import com.example.products.model.Product
import com.example.products.model.User
import com.example.products.utils.retrofit.ApiResult
import com.google.android.gms.auth.api.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginViewModel(private val repository: Repository) : ViewModel() {


    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState: StateFlow<LoginScreenState> = _uiState.asStateFlow()

    fun login(save:Boolean = false, onSuccess: (authData: AuthResponse) -> Unit) {
        auth(
            User(
                login = _uiState.value.login,
                password = _uiState.value.password
            ),
            save,
            onSuccess
        )
    }

    private fun auth(user: User, save: Boolean, onSuccess: (authData: AuthResponse) -> Unit) {
        _uiState.value = _uiState.value.copy(
            apiResult = ApiResult.Loading()
        )
        viewModelScope.launch {
            try {
                val state = ApiResult.Success(repository.login(user))
                _uiState.value = _uiState.value.copy(
                    apiResult = state,
                    Save = save
                )
                onSuccess(state.data)
            } catch (e: Exception) {
                var msg = ""
                when (e) {
                    is SocketTimeoutException -> {
                        msg = "Время ожидания истекло. " +
                                "Пожалуйста проверьте подключение к интернету"
                    }
                    is UnknownHostException -> {
                        msg = "Невозможно установить соединение. "+
                                "Пожалуйста проверьте подключение к интернету"
                    }
                    is ConnectionShutdownException -> {
                        msg = "Подключение прервано"+
                                "Пожалуйста проверьте подключение к интернету"
                    }
                    is IOException -> {
                        msg = "Сервер не отвечает. Попробуйте позже"
                    }
                    is IllegalStateException -> {
                        msg = "${e.message}"
                    }
                    else -> {
                        msg = when(e.message){
                            "HTTP 401 Unauthorized" -> "Неверный логин или пароль"
                            else-> e.message.toString()
                        }
                    }
                }
                _uiState.value = _uiState.value.copy(
                    apiResult = ApiResult.Failure(e, msg)
                )
            }
        }
    }

    fun setLogin(login: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    login = login
                )
            }
        }
    }

    fun setPassword(password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    password = password
                )
            }
        }
    }

    data class LoginScreenState(
        val login: String = "",
        var password: String = "",
        val Error: Boolean = false,
        val apiResult: ApiResult<AuthResponse> = ApiResult.Empty(),

        val Save: Boolean = false
    )

    companion object {
        class Factory(val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(repository) as T
            }
        }
    }

}