package com.example.products.viewmodel.home.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.products.repository.Repository
import com.example.products.utils.retrofit.AUTH_TYPE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AccountViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    suspend fun setToken(token: String) {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        fetchUser(token)

        _uiState.update {
            it.copy(
                isLoading = false
            )
        }
    }

    private suspend fun fetchUser(token: String) {
        val auth = "$AUTH_TYPE$token"
        try {
            val u = repository.getPerson(auth)
            with(u) {
                _uiState.update {
                    it.copy(
                        Name = name,
                        Surname = secondName,
                        LastName = middleName,
                        Login = "",
                        Password = "",
                        ConfirmPassword = "",
                    )
                }
            }
        } catch (e: Exception) {
            var msg = ""
            when (e) {
                is SocketTimeoutException -> {
                    msg = "Время ожидания истекло. " +
                            "Пожалуйста проверьте подключение к интернету"
                }
                is UnknownHostException -> {
                    msg = "Невозможно установить соединение. " +
                            "Пожалуйста проверьте подключение к интернету"
                }
                is ConnectionShutdownException -> {
                    msg = "Подключение прервано" +
                            "Пожалуйста проверьте подключение к интернету"
                }
                is IOException -> {
                    msg = "Сервер не отвечает. Попробуйте позже"
                }
                is IllegalStateException -> {
                    msg = "${e.message}"
                }
                else -> {
                    msg = when (e.message) {
                        "HTTP 401 Unauthorized" -> {
                            _uiState.update {
                                it.copy(
                                    UnAuth = true
                                )
                            }
                            "Ошибка авторизации"
                        }
                        else -> e.message.toString()
                    }
                }
            }
            _uiState.update {
                it.copy(
                    isServerError = true,
                    ServerError = msg
                )
            }
        }

    }


    fun setName(name: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    Name = name
                )
            }
            checkName(name)
        }
    }

    fun setSurname(surname: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    Surname = surname
                )
            }
            checkSurname(surname)
        }
    }

    fun setLastname(lastname: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    LastName = lastname
                )
            }
        }
    }

    fun setLogin(login: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    Login = login
                )
            }
            checkLogin(login)
        }
    }

    fun setPassword(password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    Password = password
                )
            }
            checkPassword(password)
        }
    }

    fun setConfirmPassword(password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    ConfirmPassword = password
                )
            }
            comparePasswords(_uiState.value.Password, password)
        }
    }

    private suspend fun checkLogin(login: String) {
        if (login.length < 6) {
            _uiState.update {
                it.copy(
                    LoginError = true,
                    LoginErrorValue = "Длина логина должна быть не меньше 6 символов"
                )
            }
        } /*else if (! Pattern.matches("[a-zA-Z\\d]", login)) {
            _uiState.update {
                it.copy(
                    LoginError = true,
                    LoginErrorValue = "Пароль может состоять только из латинских букв и цифр"
                )
            }
        }*/ else {
           /* val check = repository.checkLogin(login)
            if (check is CheckResult.Failure){
                _uiState.update {
                    it.copy(
                        LoginError = true,
                        LoginErrorValue = check.message
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        LoginError = false,
                        LoginErrorValue = ""
                    )
                }
            }*/
        }
    }

    private fun checkPassword(password: String) {
        if (password.length < 6) {
            _uiState.update {
                it.copy(
                    PasswordError = true,
                    PasswordErrorValue = "Длина пароля должна быть не меньше 6 символов"
                )
            }
        } /*else if (! Pattern.matches("[a-zA-Z\\d]", password)) {
            _uiState.update {
                it.copy(
                    PasswordError = true,
                    PasswordErrorValue = "Пароль может состоять только из латинских букв и цифр"
                )
            }
        }*/ else {
            _uiState.update {
                it.copy(
                    PasswordError = false,
                    PasswordErrorValue = ""
                )
            }
        }
    }

    private fun comparePasswords(password: String, confirmPassword: String) {
        if (password == confirmPassword) {
            _uiState.update {
                it.copy(
                    ConfirmPasswordError = false,
                    ConfirmPasswordErrorValue = ""
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    ConfirmPasswordError = true,
                    ConfirmPasswordErrorValue = "Пароли должны совпадать"
                )
            }
        }
    }

    private fun checkName(name: String) {
        if (name.isEmpty()) {
            _uiState.update {
                it.copy(
                    NameError = true,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    NameError = false,
                )
            }
        }
    }

    private fun checkSurname(surname: String) {
        if (surname.isEmpty()) {
            _uiState.update {
                it.copy(
                    SurnameError = true,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    SurnameError = false,
                )
            }
        }
    }

    private fun UiState.isValid(): Boolean =
        ! (NameError or
                SurnameError or
                LoginError or
                PasswordError or
                ConfirmPasswordError)

    fun Edit() {
        _uiState.update {
            it.copy(
                isEditing = true
            )
        }
    }

    fun confirmEdits(onSuccess: () -> Unit) {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            with(_uiState.value) {
                checkLogin(Login)
                checkPassword(Password)
                comparePasswords(Password, ConfirmPassword)
                checkName(Name)
                checkSurname(Surname)
            }
            if (_uiState.value.isValid()) {
                onSuccess.invoke()
                _uiState.update {
                    it.copy(
                        isEditing = false
                    )
                }
                with(_uiState.value) {
                    TODO("Добавить изменение данных пользователя")
                }
            }
            _uiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }


    data class UiState(
        val Name: String = "",
        val Surname: String = "",
        val LastName: String = "",
        val Login: String = "",
        val Password: String = "",
        val ConfirmPassword: String = "",
        val LoginError: Boolean = false,
        val LoginErrorValue: String = "",
        val PasswordError: Boolean = false,
        val PasswordErrorValue: String = "",
        val ConfirmPasswordError: Boolean = false,
        val ConfirmPasswordErrorValue: String = "",
        val NameError: Boolean = false,
        val SurnameError: Boolean = false,
        val isLoading: Boolean = false,

        val isEditing: Boolean = false,

        val UnAuth: Boolean = false,

        val ServerError: String = "",
        val isServerError: Boolean = false
    )


    companion object {
        class Factory(
            private val repository: Repository,
        ) : ViewModelProvider.NewInstanceFactory() {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AccountViewModel(repository) as T
            }
        }
    }
}