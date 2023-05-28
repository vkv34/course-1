package com.example.products.ui.screen.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.products.R
import com.example.products.model.AuthResponse
import com.example.products.navigation.AppNavigation
import com.example.products.repository.ApiRepository
import com.example.products.ui.controls.OutlineTextFieldWithValidation
import com.example.products.ui.controls.PasswordField
import com.example.products.utils.account.login
import com.example.products.utils.retrofit.ApiResult
import com.example.products.viewmodel.auth.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SavePasswordRequest
import com.google.android.gms.auth.api.identity.SignInPassword
import kotlinx.coroutines.launch

/**
 *
 * Экран авторизации
 */
@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = viewModel(
        factory = SignInViewModel.Companion.Factory(
            repository = ApiRepository()
        )
    ),
    context: Context = LocalContext.current,
) {
    val uiState by viewModel.uiState.collectAsState()
    val oneTapClient = Identity.getSignInClient(context)

    val savePasswordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
        }
    )

    LaunchedEffect(uiState.apiSignInState) {
        when (uiState.apiSignInState) {
            is ApiResult.Failure -> {
                Toast.makeText(
                    context,
                    (uiState.apiSignInState as ApiResult.Failure<AuthResponse>).msg,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ApiResult.Success -> {
                val s: SavePasswordRequest = SavePasswordRequest
                    .builder()
                    .setSignInPassword(
                        SignInPassword(uiState.login, uiState.password)
                    ).build()

                Identity.getCredentialSavingClient(context)
                    .savePassword(s).addOnSuccessListener {
                        try {
                            savePasswordLauncher.launch(
                                IntentSenderRequest
                                    .Builder(it.pendingIntent.intentSender)
                                    .build()
                            )
                        }catch(ex: Exception) {
                            Log.e("SignIn", "SignInScreen: ${ex.message}", )
                        }
                    }

                login((uiState.apiSignInState as ApiResult.Success<AuthResponse>).data.token)

                navController.navigate(AppNavigation.Home.Catalog.route)
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.sign_in),
            style = MaterialTheme.typography.displayLarge
        )
        OutlineTextFieldWithValidation(
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            value = uiState.surname,
            onValueChange = {
                viewModel.setSurname(it)
            },
            isError = uiState.isSurnameError,
            label = {
                Text(text = stringResource(id = R.string.surname))
            },
            errorMsg = uiState.surnameError
        )
        OutlineTextFieldWithValidation(
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            value = uiState.name,
            onValueChange = {
                viewModel.setName(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = uiState.isNameError,
            label = {
                Text(text = stringResource(id = R.string.name))
            },
            errorMsg = uiState.nameError
        )
        OutlineTextFieldWithValidation(
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            value = uiState.lastName,
            onValueChange = {
                viewModel.setLastname(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = false,
            label = {
                Text(text = stringResource(id = R.string.lastname))
            },
        )

        OutlineTextFieldWithValidation(
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            value = uiState.login,
            onValueChange = {
                viewModel.setLogin(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            isError = uiState.isLoginError,
            label = {
                Text(text = stringResource(id = R.string.login))
            },
            errorMsg = uiState.loginErrorValue
        )
        OutlineTextFieldWithValidation(
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            value = uiState.email,
            onValueChange = {
                viewModel.setEmail(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = uiState.isEmailError,
            label = {
                Text(text = "Почта")
            },
            errorMsg = uiState.emailError
        )

        PasswordField(
            value = uiState.password,
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            isError = uiState.isPasswordError,
            onValueChange = {
                viewModel.setPassword(it)
            })
        if (uiState.isPasswordError) {
            Text(
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                text = uiState.passwordErrorValue
            )
        }
        PasswordField(
            value = uiState.confirmPassword,
            labelText = stringResource(id = R.string.confirm_password),
            isError = uiState.isConfirmPasswordError,
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth(),
            onValueChange = {
                viewModel.setConfirmPassword(it)
            })
        if (uiState.isConfirmPasswordError) {
            Text(
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                text = uiState.confirmPasswordErrorValue
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        val coroutineScope = rememberCoroutineScope()
        Button(
            modifier = Modifier
                .padding(20.dp, 0.dp, 16.dp, 0.dp),
            onClick = {
                viewModel.signIn {}
            }) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))

            }
            Text(
                modifier = Modifier.padding(10.dp),
                text = "Зарегистрироваться",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
