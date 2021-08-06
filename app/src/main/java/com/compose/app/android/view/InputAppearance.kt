package com.compose.app.android.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LargeTextInputField(text: TextFieldValue, hint: String, valueCallback: (TextFieldValue) -> Unit,
                        icon: ImageVector, contentDescription: String, passwordType: Boolean = false) {
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

@Preview(showBackground = true)
@Composable
fun E() {
    LargeTextInputField(
        text = TextFieldValue(""),
        hint = "Email address",
        valueCallback = { /*TODO*/ },
        icon = Icons.Rounded.Email,
        contentDescription = "Icon"
    )
}