package com.example.products.ui.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.products.R

/**
 *
 * Поле пароля
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    value: String = "",
    isError: Boolean = false,
    labelText: String = "Пароль",
    onValueChange: (String) -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }

    val icon = if (isVisible)
        painterResource(id = R.drawable.ic_visibility_off_24)
    else painterResource(id = R.drawable.ic_visibility_24)

    OutlinedTextField(
        value = value,
        shape = MaterialTheme.shapes.small,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = isError,
        label = {
            Text(text = labelText)
        },
        modifier = modifier,
        trailingIcon = {
            Icon(painter = icon,
                contentDescription = "visibility icon",
                Modifier.clickable {
                    isVisible = ! isVisible
                })
        },
        visualTransformation = if (isVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        onValueChange = {
            onValueChange(it)
        })
}
