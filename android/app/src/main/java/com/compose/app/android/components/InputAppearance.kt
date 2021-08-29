package com.compose.app.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.compose.app.android.theme.IconEyeClosed
import com.compose.app.android.theme.IconEyeOpen

@Composable
fun LargeTextInputField(
    text: TextFieldValue,
    hint: String,
    valueCallback: (TextFieldValue) -> Unit,
    icon: Painter,
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
            backgroundColor = colorResource(id = com.compose.app.android.R.color.neutral_gray),
            cursorColor = MaterialTheme.colors.onBackground,
            disabledLabelColor = colorResource(id = com.compose.app.android.R.color.neutral_gray),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = valueCallback,
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        leadingIcon = @Composable {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colors.onBackground
            )
        },
        trailingIcon = @Composable {
            if (passwordType) {
                val image = if (passwordVisibility) {
                    IconEyeOpen
                } else {
                    IconEyeClosed
                }
                IconButton(
                    onClick = {
                        passwordVisibility = !passwordVisibility
                    },
                    content = {
                        Icon(
                            painter = painterResource(id = image),
                            "TODO"
                        )
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
fun ExperimentalTextOnlyTextField(
    textFieldValue: TextFieldValue,
    hint: String,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle
) {
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { value ->
                onValueChange(value)
            },
            textStyle = textStyle,
            cursorBrush = SolidColor(MaterialTheme.colors.onBackground)
        )
        if (textFieldValue.text.isEmpty()) {
            Text(
                text = hint,
                style = textStyle,
                color = MaterialTheme.colors.onSurface.copy(0.5F)
            )
        }
    }
}
