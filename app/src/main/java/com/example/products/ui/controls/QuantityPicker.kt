package com.example.products.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.products.R

/**
 *
 * Управление количеством
 */
@Composable
fun QuantityPicker(quantity: Int, onQuantityChanged: (Int) -> Unit, isEditable: Boolean = true) {

    Box(
        /*modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                1.dp,
                MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.medium,
            )
            .background(
                color = MaterialTheme.colorScheme.inverseSurface.copy(
                    alpha = 0.3f
                )
            )*/
//            .padding(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isEditable) {
                IconButton(onClick = { onQuantityChanged(quantity - 1) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.minus),
                        contentDescription = Icons.TwoTone.Add.name
                    )
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .border(
                            width = 1.dp,

                            color = if (isEditable)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.0f),

                            shape = RoundedCornerShape(8.dp),
                        )
                        .background(
                            color = if (isEditable)
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.3f
                                )
                            else
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.0f
                                )
                        )
                        .padding(6.dp)
                ) {
                    BasicTextField(
                        modifier = Modifier
                            .width((quantity.toString().length * 10).dp)
                            .height(15.dp)
                            .align(Alignment.Center),
                        value = quantity.toString(),
                        textStyle = MaterialTheme.typography.labelLarge
                            .copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            ),
                        maxLines = 1,
                        readOnly = ! isEditable,
                        onValueChange = {
                            if (it.isBlank())
                                onQuantityChanged(0)
                            else if (("^\\d+\$").toRegex().matches(it)) {
                                val sb = StringBuilder(it)
                                while (true) {
                                    if (sb.length == 1)
                                        break
                                    if (sb.first() == '0') {
                                        sb.deleteCharAt(0)
                                    } else {
                                        break
                                    }
                                }

                                onQuantityChanged(sb.toString().toInt())
                            }

                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
                Text(
                    text = " шт.",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            if (isEditable) {
                IconButton(onClick = { onQuantityChanged(quantity + 1) }) {
                    Icon(
                        imageVector = Icons.TwoTone.Add,
                        contentDescription = Icons.TwoTone.Add.name
                    )
                }
            }
        }
    }
}