package com.example.products.ui.screen.auth

import android.app.Activity.RESULT_OK
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.products.navigation.AppNavigation
import com.example.products.repository.ApiRepository
import com.example.products.ui.controls.PasswordField
import com.example.products.utils.account.login
import com.example.products.utils.retrofit.ApiResult
import com.example.products.viewmodel.auth.LoginViewModel
import com.google.android.gms.auth.api.identity.*
import kotlinx.coroutines.launch


/**
 * Экран авторизации
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Companion.Factory(
            ApiRepository()
        )
    ),
    context: Context = LocalContext.current,
) {


    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    fun login() {
        viewModel.login {
            navController.navigate(AppNavigation.Home.route) {
                popUpTo(AppNavigation.Auth.route) {
                    inclusive = true
                }
            }
            coroutineScope.launch {
                login(it.token)
            }

        }
    }

    val oneTapClient = Identity.getSignInClient(context)
    val getPasswordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode != RESULT_OK) {
                return@rememberLauncherForActivityResult
            }

            with(oneTapClient.getSignInCredentialFromIntent(it.data)) {
                viewModel.setLogin(id)
                password?.let { it1 -> viewModel.setPassword(it1) }
                login()
            }
        }
    )
    val savePasswordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
        }
    )

    LaunchedEffect(Unit) {

        val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            ).build()
        oneTapClient
            .beginSignIn(signInRequest)
            .addOnSuccessListener {
                getPasswordLauncher.launch(
                    IntentSenderRequest
                        .Builder(it.pendingIntent.intentSender)
                        .build()
                )
            }
            .addOnCanceledListener {
                Log.d("LoginScreen", "LoginScreen: one tap canceled")
            }
            .addOnFailureListener {
                Log.e("LoginScreen", "LoginScreen: ${it.message}")
            }
    }

    LaunchedEffect(uiState.Save) {
        if (uiState.Save) {
            val s: SavePasswordRequest = SavePasswordRequest
                .builder()
                .setSignInPassword(
                    SignInPassword(uiState.login, uiState.password)
                ).build()

            Identity.getCredentialSavingClient(context)
                .savePassword(s).addOnSuccessListener {
                    savePasswordLauncher.launch(
                        IntentSenderRequest
                            .Builder(it.pendingIntent.intentSender)
                            .build()
                    )
                }


        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вход",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Для продолжения необходимо войти",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
            color = MaterialTheme.colorScheme.onBackground

        )
        OutlinedTextField(
            value = uiState.login,
            shape = MaterialTheme.shapes.small,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = uiState.apiResult is ApiResult.Failure,
            enabled = uiState.apiResult !is ApiResult.Loading,
            label = {
                Text(text = "Логин")
            },
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
            onValueChange = {
                viewModel.setLogin(it)
            })
        PasswordField(
            value = uiState.password,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
            isError = uiState.apiResult is ApiResult.Failure,
            onValueChange = {
                viewModel.setPassword(it)
            })
        if (uiState.apiResult is ApiResult.Failure) {
            Text(
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error,
                text = (uiState.apiResult as ApiResult.Failure).msg
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 0.dp),
            enabled = uiState.apiResult !is ApiResult.Loading,
            onClick = {
                login()
            }) {
            if (uiState.apiResult is ApiResult.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = "Войти")
        }
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 0.dp),
            onClick = { navController.navigate(AppNavigation.Auth.SignIn.route) }
        ) {
            Text(text = "Регистрация")
        }
    }

}

