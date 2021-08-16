package com.compose.app.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LargeTextInputField(
    text: TextFieldValue,
    hint: String,
    valueCallback: (TextFieldValue) -> Unit,
    icon: ImageVector,
    contentDescription: String,
    passwordType: Boolean = false
) {
    var passwordVisibility by remember { mutableStateOf(true) }
    val keyboardType = if (passwordType) {
        KeyboardOptions(keyboardType = KeyboardType.Password)
    } else {
        KeyboardOptions(keyboardType = KeyboardType.Text)
    }
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp),
        value = text,
        placeholder = @Composable {
            Text(
                text = hint,
                style = MaterialTheme.typography.body2,
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = colorResource(id = com.compose.app.android.R.color.button_neutral_background_color),
            cursorColor = Color.Black,
            disabledLabelColor = colorResource(id = com.compose.app.android.R.color.button_neutral_background_color),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = valueCallback,
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        leadingIcon = @Composable {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colors.onBackground
            )
        },
        trailingIcon = @Composable {
            if (passwordType) {
                val image = if (passwordVisibility) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                IconButton(
                    onClick = {
                        passwordVisibility = !passwordVisibility
                    },
                    content = {
                        Icon(imageVector = image, "")
                    }
                )
            }
        },
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = keyboardType
    )
}

@Composable
fun TextOnlyTextInput(
    textFieldValue: TextFieldValue,
    hint: String,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle
) {
    TextField(
        modifier = modifier,
        value = textFieldValue,
        textStyle = textStyle,
        placeholder = @Composable {
            Text(
                text = hint,
                style = textStyle,
                modifier = Modifier.padding(0.dp)
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.onSurface,
            disabledLabelColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = {
            onValueChange(it)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}
