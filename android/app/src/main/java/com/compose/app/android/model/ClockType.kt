package com.compose.app.android.model

/**
 * Used in DatePickerSheetView, to manage the state
 * of the clock: whether the user is picking the hour
 * or the minute of their due date
 */
enum class ClockType {
    HourSelection, MinuteSelection
}