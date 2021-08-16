package com.compose.app.android.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.compose.app.android.model.ExpandableFABItem
import com.compose.app.android.model.ExpandableFABState


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
            backgroundColor = colorResource(id = R.color.button_neutral_background_color)
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

@Composable
fun AddNoteTaskMenuFAB(
    icon: Painter,
    contentDescription: String,
    onExpansion: (state: ExpandableFABState) -> Unit,
    expandedState: ExpandableFABState,
    modifier: Modifier,
    menuItems: List<ExpandableFABItem>
) {
    val transitionUpdate = updateTransition(targetState = expandedState, label = "Rotating FAB")
    val iconRotation: Float by transitionUpdate.animateFloat(label = "Icon rotation") { state ->
        if (state == ExpandableFABState.EXPANDED) 90F else 0F
    }
    val actionRotation by transitionUpdate.animateFloat(label = "FAB roation") { state ->
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
                onClick = item.onClick,
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
                elevation = FloatingActionButtonDefaults.elevation(1.dp),
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
