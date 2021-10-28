package com.compose.app.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
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

/**
 * TextField that fills the width of the screen, with a
 * rounded gray background and an icon to the left.
 * @param text - the TextFieldValue to be used for this
 * text field
 * @param hint - Placeholder text to be shown when the
 * field is empty
 * @param valueCallback - Function to be invoked when the
 * TextFieldValue changes
 * @param icon - The icon to be displayed on the left side
 * of the text field
 * @param contentDescription - A string accessibility content
 * description of the icon used
 * @param passwordType - Indicate true if this field is a secure
 * input field, to hide the text and allow the user to show and
 * hide when needed.
 */
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

/**
 * A TextField with only the text and a hint, no border, underline or
 * other decoration, like the XML EditText.
 * @param textFieldValue - The TextFieldValue to be used in the
 * TextField
 * @param hint - A placeholder text to be shown when the field is
 * empty, at a lighter color
 * @param onValueChange - Function passing the new TextFieldValue to
 * be called when the text or selection changes, usually where the
 * new value is assigned to the TextFieldValue
 * @param modifier - A modifier to be applied to the view
 * @param textStyle - TextStyle to be applied to the TextField and the
 * hint.
 */
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
        // Hint when empty
        if (textFieldValue.text.isEmpty()) {
            Text(
                text = hint,
                style = textStyle,
                color = MaterialTheme.colors.onSurface.copy(0.5F)
            )
        }
    }
}
