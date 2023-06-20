package com.example.products.ui.screen.home.account

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.products.navigation.ROOT_ROUTE
import com.example.products.repository.ApiRepository
import com.example.products.ui.controls.PasswordField
import com.example.products.utils.account.getToken
import com.example.products.viewmodel.home.account.AccountViewModel
import kotlinx.coroutines.launch

/**
 * Экран аккаунта
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AccountScreen(
    rootNavController: NavHostController,
    homeNavController: NavHostController,
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    context: Context = LocalContext.current,
    viewModel: AccountViewModel = viewModel(
        factory = AccountViewModel.Companion.Factory(
            repository = ApiRepository()
        )
    ),
) {
    val uiState by viewModel.uiState.collectAsState()


    val coroutineScope = rememberCoroutineScope()
    fun logout(){
        coroutineScope.launch {
            com.example.products.utils.account.logout()
            rootNavController.navigate(ROOT_ROUTE){
                popUpTo(ROOT_ROUTE)
            }
            context.getActivity()?.recreate()
        }
    }

    LaunchedEffect(key1 = ""){
        val token = getToken()
        viewModel.setToken(token)
    }

    LaunchedEffect(key1 = uiState.UnAuth){
        if (uiState.UnAuth){
            logout()
        }
    }

    if (uiState.isServerError){
        coroutineScope.launch {
            snackbarHostState.showSnackbar(uiState.ServerError)
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*AccountTopBar(
            isEdit = uiState.isEditing,
            onEditClick = {
                if (!it){
                    viewModel.Edit()
                }else{
                    viewModel.confirmEdits{
                        Toast.makeText(context, "Успешно изменено", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ){
            navController.navigate(AppNavigation.Auth.Login.route){
                popUpTo(AppNavigation.Home.route)
            }
            with(context){
                with(getSharedPreferences(packageName, Context.MODE_PRIVATE).edit()){
                    putInt("userId", -1)
                    apply()
                }
            }
        }*/
        OutlinedTextField(
            value = uiState.Surname,
            shape = MaterialTheme.shapes.small,
            isError = uiState.SurnameError,
            readOnly = ! uiState.isEditing,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = {
                Text(text = "Фамилия")
            },
            modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
            onValueChange = {
                viewModel.setSurname(it)
            })
        if (uiState.SurnameError) {
            Text(
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                text = "Поле фамилия должно быть заполнено"
            )
        }
        OutlinedTextField(
            value = uiState.Name,
            shape = MaterialTheme.shapes.small,
            readOnly = ! uiState.isEditing,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = uiState.NameError,
            label = {
                Text(text = "Имя")
            },
            modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
            onValueChange = {
                viewModel.setName(it)
            })
        if (uiState.NameError) {
            Text(
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                text = "Поле имя должно быть заполнено"
            )
        }
        OutlinedTextField(
            value = uiState.LastName,
            shape = MaterialTheme.shapes.small,
            readOnly = ! uiState.isEditing,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = {
                Text(text = "Отчество")
            },
            modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
            onValueChange = {
                viewModel.setLastname(it)
            })


        if (uiState.isEditing) {

            OutlinedTextField(
                value = uiState.Login,
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                readOnly = ! uiState.isEditing,
                isError = uiState.LoginError,
                label = {
                    Text(text = "Логин")
                },
                modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                onValueChange = {
                    viewModel.setLogin(it)
                })
            if (uiState.LoginError) {
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    text = uiState.LoginErrorValue
                )
            }


            PasswordField(
                value = uiState.Password,
                modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                isError = uiState.PasswordError,
                onValueChange = {
                    viewModel.setPassword(it)
                })
            if (uiState.PasswordError) {
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    text = uiState.PasswordErrorValue
                )
            }
            PasswordField(
                value = uiState.ConfirmPassword,
                labelText = "Повторите пароль",
                isError = uiState.ConfirmPasswordError,
                modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                onValueChange = {
                    viewModel.setConfirmPassword(it)
                })
            if (uiState.ConfirmPasswordError) {
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    text = uiState.ConfirmPasswordErrorValue
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = { logout()}) {
            Text(text = "Выйти")
        }
    }


}

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}