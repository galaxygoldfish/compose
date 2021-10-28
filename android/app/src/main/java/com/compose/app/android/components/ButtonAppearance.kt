package com.compose.app.android.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.compose.app.android.R
import com.compose.app.android.model.ExpandableFAB
import com.compose.app.android.model.ExpandableFABState

/**
 * A button, similar to the LargeTextField, filling the screen
 * width with an icon to the left and text in the center.
 * @param text - The main text to be displayed by the button
 * @param icon - The painter containing the icon to be
 * displayed to the left of the text
 * @param contentDescription - An accessibility content
 * description of the icon used
 * @param color - The background color of the button
 * @param onClick - Function to be invoked when the button is
 * clicked.
 */
@Composable
fun FullWidthButton(
    text: String,
    icon: Painter,
    contentDescription: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .size(width = 1000.dp, height = 60.dp)
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        elevation = elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = Color.Black
            )
            Text(
                text = text,
                style = MaterialTheme.typography.button,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 1.dp, start = 24.dp),
                color = Color.Black,
            )
        }
    }
}

/**
 * A smaller button, adapting to the text used with only
 * the text and no icon.
 * @param text - The text to be displayed on the button
 * @param color - The background color of the button
 * @param onClick - Function to be invoked when the user
 * clicks the button
 */
@Composable
fun TextOnlyButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp)
            .size(height = 45.dp, width = Dp.Unspecified),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        elevation = elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Box {
            Text(
                text = text,
                style = MaterialTheme.typography.button,
                modifier = Modifier.align(Alignment.TopCenter),
                color = Color.Black
            )
        }
    }
}

/**
 * A button with an icon and gray rounded background.
 * @param icon - A painter containing the icon to be used
 * @param contentDescription - Accessibility content description
 * of the icon used
 * @param onClick - Function to be invoked when the user clicks
 * the button
 */
@Composable
fun IconOnlyButton(
    icon: Painter,
    onClick: () -> Unit,
    contentDescription: String
) {
    Button(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp, end = 15.dp)
            .size(height = 45.dp, width = Dp.Unspecified),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.neutral_gray)
        ),
        elevation = elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Box {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
            )
        }
    }
}

/**
 * A floating action button used on the center of the bottom
 * navigation bar in ProductivityView. When clicked, it throws
 * two extra buttons above it while rotating to create an animated
 * effect.
 * @see ExpandableFABState
 * @see ExpandableFAB
 * @param icon - The icon of the main floating action button that
 * is always visible
 * @param contentDescription - Accessibility content description
 * of the icon used on the main button
 * @param onExpansion - Function invoked to change the state of
 * the expansion of the button
 * @param expandedState - The current ExpandableFABState representing
 * the initial state of the button
 * @param modifier - Modifier to be applied to the button container
 * @param menuItems - A list of ExpandableFAB, storing their properties
 */
@Composable
fun AddNoteTaskMenuFAB(
    icon: Painter,
    contentDescription: String,
    onExpansion: (state: ExpandableFABState) -> Unit,
    expandedState: ExpandableFABState,
    modifier: Modifier,
    menuItems: List<ExpandableFAB>
) {
    val transitionUpdate = updateTransition(targetState = expandedState, label = "Rotating FAB")
    val iconRotation: Float by transitionUpdate.animateFloat(label = "Icon rotation") { state ->
        if (state == ExpandableFABState.EXPANDED) 90F else 0F
    }
    val actionRotation by transitionUpdate.animateFloat(label = "FAB rotation") { state ->
        if (state == ExpandableFABState.EXPANDED) 45F else 0F
    }
    val scaleTransition = transitionUpdate.animateFloat(label = "Menu item scale") { state ->
        if (state == ExpandableFABState.EXPANDED) 47F else 0F
    }
    val colorTransition by transitionUpdate.animateColor(label = "Base FAB color") { state ->
        if (state == ExpandableFABState.EXPANDED) colorResource(id = R.color.text_color_enabled)
        else colorResource(id = R.color.deep_sea)
    }
    val iconColorTransition by transitionUpdate.animateColor(label = "Base FAB icon color") { state ->
        if (state == ExpandableFABState.EXPANDED) colorResource(id = R.color.text_color_reverse)
        else colorResource(id = R.color.black)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        menuItems.forEachIndexed { index, item ->
            FloatingActionButton(
                onClick = {
                    item.onClick.invoke()
                    onExpansion(ExpandableFABState.COLLAPSED)
                },
                content = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.contentDescription,
                        tint = colorResource(id = R.color.text_color_reverse)
                    )
                },
                modifier = Modifier.padding(bottom = if (index % 2 == 0) 12.dp else 20.dp)
                    .size(scaleTransition.value.dp),
                backgroundColor = colorResource(id = R.color.text_color_enabled),
                elevation = FloatingActionButtonDefaults.elevation(10.dp),
                shape = RoundedCornerShape(10.dp)
            )
        }
        FloatingActionButton(
            onClick = {
                onExpansion(
                    if (transitionUpdate.currentState == ExpandableFABState.EXPANDED) {
                        ExpandableFABState.COLLAPSED
                    } else {
                        ExpandableFABState.EXPANDED
                    }
                )
            },
            modifier = modifier.rotate(actionRotation),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            ),
            backgroundColor = colorTransition,
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                modifier = Modifier.rotate(iconRotation),
                tint = iconColorTransition
            )
        }
    }
}
