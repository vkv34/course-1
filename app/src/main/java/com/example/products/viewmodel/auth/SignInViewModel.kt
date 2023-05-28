package com.example.products.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.products.model.AuthResponse
import com.example.products.model.Person
import com.example.products.model.User
import com.example.products.repository.Repository
import com.example.products.utils.retrofit.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignInViewModel(private val repository: Repository) : ViewModel() {


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    fun setName(name: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    name = name
                )
            }
            checkName(name)
        }
    }

    fun setSurname(surname: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    surname = surname
                )
            }
            checkSurname(surname)
        }
    }

    fun setLastname(lastname: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    lastName = lastname
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
            checkLogin(login)
        }
    }

    fun setPassword(password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    password = password
                )
            }
            checkPassword(password)
        }
    }

    fun setConfirmPassword(password: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    confirmPassword = password
                )
            }
            comparePasswords(_uiState.value.password, password)
        }
    }

    fun setEmail(email: String){
        if (email.isBlank())
            _uiState.update {
                it.copy(
                    emailError = "Поле почта не может быть пустым",
                    isEmailError = true,
                    email = email
                )
            }
        else if(!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.\\w+\$", email)){
            _uiState.update {
                it.copy(
                    emailError = "Почта не соответсвует формату",
                    isEmailError = true,
                    email = email
                )
            }
        }
        else{
            _uiState.update {
                it.copy(
                    emailError = "",
                    isEmailError = false,
                    email = email
                )
            }
        }
    }

    private suspend fun checkLogin(login: String) {
        if (login.length < 6) {
            _uiState.update {
                it.copy(
                    isLoginError = true,
                    loginErrorValue = "Длина логина должна быть не меньше 6 символов"
                )
            }
        } else if (! Pattern.matches("^(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{6,}\$", login)) {
            _uiState.update {
                it.copy(
                    isLoginError = true,
                    loginErrorValue = "Логин может состоять только из латинских букв и цифр"
                )
            }
        } else {
            viewModelScope.launch {
                repository.checkLogin(login)
                    .collect { apiResult ->
                        if (apiResult is ApiResult.Failure) {
                            _uiState.update {
                                it.copy(
                                    isLoginError = true,
                                    loginErrorValue = apiResult.msg
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoginError = false,
                                    loginErrorValue = ""
                                )
                            }
                        }
                    }
            }


        }
    }

    private fun checkPassword(password: String) {
        if (password.length < 6) {
            _uiState.update {
                it.copy(
                    isPasswordError = true,
                    passwordErrorValue = "Длина пароля должна быть не меньше 6 символов"
                )
            }
        } else if (! Pattern.matches("^(?=.*[A-Z])(?=.*[!@#\$%^&*])(?=.*\\d)[A-Za-z\\d!@#\$%^&*]+\$", password)) {
            _uiState.update {
                it.copy(
                    isPasswordError = true,
                    passwordErrorValue = "Пароль может состоять только из латинских букв и цифр"
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isPasswordError = false,
                    passwordErrorValue = ""
                )
            }
        }
    }

    private fun comparePasswords(password: String, confirmPassword: String) {
        if (password == confirmPassword) {
            _uiState.update {
                it.copy(
                    isConfirmPasswordError = false,
                    confirmPasswordErrorValue = ""
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isConfirmPasswordError = true,
                    confirmPasswordErrorValue = "Пароли должны совпадать"
                )
            }
        }
    }

    private fun checkName(name: String) {
        if (name.isEmpty()) {
            _uiState.update {
                it.copy(
                    isNameError = true,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isNameError = false,
                )
            }
        }
    }

    private fun checkSurname(surname: String) {
        if (surname.isEmpty()) {
            _uiState.update {
                it.copy(
                    isSurnameError = true,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isSurnameError = false,
                )
            }
        }
    }

    private fun UiState.isValid(): Boolean =
        ! (isNameError or
                isSurnameError or
                isLoginError or
                isPasswordError or
                isConfirmPasswordError)

    fun signIn(onSuccess: (user: User) -> Unit){
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {

            with(_uiState.value) {
                checkLogin(login)
                checkPassword(password)
                comparePasswords(password, confirmPassword)
                checkName(name)
                checkSurname(surname)
            }
            if (_uiState.value.isValid()) {

                val user = with(_uiState.value) {
                    User(
                        login = login,
                        password = password
                    )
                }
                val person = with(_uiState.value) {
                    Person(
                        name = name,
                        secondName = surname,
                        middleName = lastName,
                        email = email
                    )
                }


                viewModelScope.launch {
                    repository.signIn(
                        user,
                        person
                    ).collect{apiResult->
                        if (apiResult is ApiResult.Success){
                            onSuccess.invoke(user)
                        }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                apiSignInState = apiResult
                            )
                        }
                    }
                }
            }
        }

    }


    data class UiState(
        val name: String = "",
        val isNameError: Boolean = false,
        val nameError: String = "Поле имя не может быть пустым",

        val surname: String = "",
        val isSurnameError: Boolean = false,
        val surnameError: String = "Поле фамилия не может быть пустым",

        val lastName: String = "",

        val login: String = "",
        val isLoginError: Boolean = false,
        val loginErrorValue: String = "",

        val password: String = "",
        val isPasswordError: Boolean = false,
        val passwordErrorValue: String = "",

        val confirmPassword: String = "",
        val isConfirmPasswordError: Boolean = false,
        val confirmPasswordErrorValue: String = "",

        val email: String = "",
        val emailError: String = "",
        val isEmailError: Boolean = false,

        val isLoading: Boolean = false,

        val apiSignInState: ApiResult<AuthResponse> = ApiResult.Empty()
    )

    companion object {
        class Factory(val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignInViewModel(repository) as T
            }
        }
    }

}