package com.compose.app.android.model

import androidx.compose.ui.graphics.painter.Painter

/**
 * Model class to store properties of each FAB item
 * in the expandable FAB used in the bottom bar from
 * ProductivityView
 * @param icon - Icon displayed in the center of the button
 * @param contentDescription - Accessibility content
 * description of the icon used
 * @param label - Label (not displayed in the UI) to keep
 * track of which button is which
 * @param onClick - Invoked when the user clicks the button
 */
data class ExpandableFAB(
    val icon: Painter,
    val contentDescription: String,
    val label: String,
    val onClick: () -> Unit
)

/**
 * Used to manage the state of the expandable button,
 * whether it is showing the other items or is they are
 * hidden
 */
enum class ExpandableFABState {
    COLLAPSED,
    EXPANDED
}
