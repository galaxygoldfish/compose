package com.compose.app.android.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.compose.app.android.R
import com.compose.app.android.components.SheetHandle
import com.compose.app.android.components.TextOnlyButton
import com.compose.app.android.model.ClockType
import com.compose.app.android.theme.IconLeftArrowSmall
import com.compose.app.android.theme.IconRightArrowSmall
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.time.Year
import java.time.YearMonth
import java.util.Calendar
import kotlinx.coroutines.launch

// TODO: Fix calendar grid so that each day is displayed under correct weekday

// TODO: Handle selected date and send this data back to view host

@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun DatePickerSheetView(
    monthDayState: MutableState<String>,
    timeHourState: MutableState<String>,
    interactionMonitor: MutableState<Boolean>
) {

    val monthArrayResource = stringArrayResource(id = R.array.month_list)
    val weekdays = stringArrayResource(id = R.array.weekday_list_abbreviated)

    val composeAsync = rememberCoroutineScope()
    val textColorDefault =  MaterialTheme.colors.onBackground

    val calendar = Calendar.getInstance()
    val monthIndex = calendar[Calendar.MONTH]
    val dayIndex = calendar[Calendar.DAY_OF_MONTH]

    val calendarMinute = calendar[Calendar.MINUTE]
    val editedMinute = if (calendarMinute.toString().length == 1) "0$calendarMinute" else calendarMinute.toString()

    val currentMonth = remember { mutableStateOf(monthArrayResource[monthIndex]) }
    val currentYear = remember { mutableStateOf(Year.now().value.toString()) }
    val selectedDayIndex = remember { mutableStateOf(dayIndex) }

    val selectedHour = remember {
        mutableStateOf(
            (if (calendar[Calendar.HOUR] == 0) 12 else calendar[Calendar.HOUR]).toString()
        )
    }
    val selectedMinute = remember { mutableStateOf(editedMinute) }
    val selectionAMPM = remember { mutableStateOf(calendar[Calendar.AM_PM]) }

    val viewPagerState = rememberPagerState(pageCount = 2)

    monthDayState.value = "${currentMonth.value} ${selectedDayIndex.value}"
    timeHourState.value = "${selectedHour.value}:${selectedMinute.value} ${if (selectionAMPM.value == 0) "AM" else "PM"}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.neutral_gray))
    ) {
        SheetHandle()
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, top = 15.dp, end = 15.dp)
        ) {
            Column {
                Text(
                    text = "${currentMonth.value} ${selectedDayIndex.value}, ${currentYear.value}",
                    style = MaterialTheme.typography.h6,
                    color =  if (viewPagerState.currentPage == 0) {
                        textColorDefault
                    } else {
                        textColorDefault.copy(0.5F)
                    },
                    modifier = Modifier.clickable {
                        composeAsync.launch {
                            viewPagerState.animateScrollToPage(0)
                        }
                    }
                )
                Text(
                    text = "${selectedHour.value}:${selectedMinute.value} ${if (selectionAMPM.value == 0) "AM" else "PM"}",
                    style = MaterialTheme.typography.h6,
                    color = if (viewPagerState.currentPage == 1) {
                        textColorDefault
                    } else {
                        textColorDefault.copy(0.5F)
                    },
                    modifier = Modifier.clickable {
                        composeAsync.launch {
                            viewPagerState.animateScrollToPage(1)
                        }
                    }
                )
            }
            Row {
                IconButton(
                    onClick = {
                        if (monthArrayResource.indexOf(currentMonth.value) == 0) {
                            currentMonth.value = monthArrayResource[11]
                            currentYear.value = (currentYear.value.toInt() - 1).toString()
                        } else {
                            val oldMonth = currentMonth.value
                            currentMonth.value =
                                monthArrayResource[monthArrayResource.indexOf(oldMonth) - 1]
                        }
                    },
                    content = @Composable {
                        Icon(
                            painter = painterResource(id = IconLeftArrowSmall),
                            contentDescription = null
                        )
                    }
                )
                IconButton(
                    onClick = {
                        if (monthArrayResource.indexOf(currentMonth.value) == 11) {
                            val oldYearValue = currentYear.value.toLong()
                            currentMonth.value = monthArrayResource[0]
                            currentYear.value = (oldYearValue + 1).toString()
                        } else {
                            currentMonth.value =
                                monthArrayResource[monthArrayResource.indexOf(currentMonth.value) + 1]
                        }
                    },
                    content = @Composable {
                        Icon(
                            painter = painterResource(id = IconRightArrowSmall),
                            contentDescription = null
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = viewPagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> CalendarDayPicker(
                    weekdayStringArray = weekdays,
                    monthStringArray = monthArrayResource,
                    currentYear = currentYear,
                    currentMonth = currentMonth,
                    selectedDayIndex = selectedDayIndex,
                    interactionMonitor = interactionMonitor
                )
                1 -> TimeHourPicker(
                    selectionAMPM = selectionAMPM,
                    selectedHour = selectedHour,
                    selectedMinute = selectedMinute,
                    interactionMonitor = interactionMonitor
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun CalendarDayPicker(
    weekdayStringArray: Array<String>,
    monthStringArray: Array<String>,
    currentYear: MutableState<String>,
    currentMonth: MutableState<String>,
    selectedDayIndex: MutableState<Int>,
    interactionMonitor: MutableState<Boolean>
) {
    Column {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.background.copy(
                if (isSystemInDarkTheme()) 0.3F else 0.9F
            ),
            modifier = Modifier.padding(15.dp),
            elevation = 0.dp,
            content = @Composable {
                LazyVerticalGrid(
                    cells = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    content = {
                        itemsIndexed(
                            items = weekdayStringArray,
                            itemContent = { _, item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, bottom = 10.dp)
                                ) {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.overline,
                                        color = colorResource(id = R.color.text_color_disabled),
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        )
                        items(
                            YearMonth.of(
                                currentYear.value.toInt(),
                                monthStringArray.indexOf(currentMonth.value) + 1
                            ).lengthOfMonth()
                        ) { index ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(45.dp)
                                    .padding(3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (selectedDayIndex.value == index + 1) {
                                            colorResource(id = R.color.deep_sea)
                                        } else {
                                            colorResource(id = R.color.neutral_gray)
                                        }
                                    )
                                    .clickable {
                                        selectedDayIndex.value = index + 1
                                        interactionMonitor.value = true
                                    }
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                )
            }
        )
    }
}

@Composable
fun TimeHourPicker(
    selectionAMPM: MutableState<Int>,
    selectedMinute: MutableState<String>,
    selectedHour: MutableState<String>,
    interactionMonitor: MutableState<Boolean>
) {

    val hourTextStrings = listOf(" 12 ", " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", " 9 ", " 10 ", " 11 ")
    val minuteTextStrings = listOf("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")
    val hourSelectionIndex = remember { mutableStateOf(0) }
    val minuteSelectionIndex = remember { mutableStateOf(0) }

    val currentClockRes = remember { mutableStateOf(hourTextStrings) }
    val currentIndexRes = remember { mutableStateOf(hourSelectionIndex) }

    val currentClockType = remember { mutableStateOf(ClockType.HourSelection) }
    val asyncScope = rememberCoroutineScope()
    val baseTextColor = MaterialTheme.colors.onBackground

    @Composable
    fun Modifier.clockTextExtras(text: String) : Modifier {
        return this
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (currentClockRes.value.indexOf(text) != currentIndexRes.value.value) {
                    Color.Transparent
                } else {
                    colorResource(id = R.color.deep_sea)
                }
            )
            .clickable {
                interactionMonitor.value = true
                asyncScope.launch {
                    val editedText = text.replace(" ", "")
                     when (currentClockType.value) {
                        ClockType.HourSelection -> {
                            selectedHour.value = editedText
                        }
                        ClockType.MinuteSelection -> {
                            selectedMinute.value = editedText
                        }
                    }
                    currentIndexRes.value.value = currentClockRes.value.indexOf(text)
                }
            }
            .padding(10.dp)
    }

    currentClockRes.value = if (currentClockType.value == ClockType.HourSelection) {
        hourTextStrings
    } else {
        minuteTextStrings
    }

    currentIndexRes.value = if (currentClockType.value == ClockType.HourSelection) {
        hourSelectionIndex
    } else {
        minuteSelectionIndex
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        MaterialTheme.colors.background.copy(
                            if (isSystemInDarkTheme()) 0.3F else 0.9F
                        )
                    )
                    .size(210.dp)
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.onSurface)
                        .size(3.dp)
                )
                Text(
                    text = currentClockRes.value[0],
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .clockTextExtras(text = currentClockRes.value[0])
                )
                Text(
                    text = currentClockRes.value[1],
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(start = 85.dp)
                        .clockTextExtras(text = currentClockRes.value[1])
                )
                Text(
                    text = currentClockRes.value[2],
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(bottom = 85.dp)
                        .clockTextExtras(text = currentClockRes.value[2])
                )
                Text(
                    text = currentClockRes.value[3],
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clockTextExtras(text = currentClockRes.value[3])
                )
                Text(
                    text = currentClockRes.value[4],
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(top = 85.dp)
                        .clockTextExtras(text = currentClockRes.value[4])
                )
                Text(
                    text = currentClockRes.value[5],
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 85.dp)
                        .clockTextExtras(text = currentClockRes.value[5])
                )
                Text(
                    text = currentClockRes.value[6],
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .clockTextExtras(text = currentClockRes.value[6])
                )
                Text(
                    text = currentClockRes.value[7],
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(end = 85.dp)
                        .clockTextExtras(text = currentClockRes.value[7])
                )
                Text(
                    text = currentClockRes.value[8],
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 85.dp)
                        .clockTextExtras(text = currentClockRes.value[8])
                )
                Text(
                    text = currentClockRes.value[9],
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clockTextExtras(text = currentClockRes.value[9])
                )
                Text(
                    text = currentClockRes.value[10],
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(bottom = 85.dp)
                        .clockTextExtras(text = currentClockRes.value[10])
                )
                Text(
                    text = currentClockRes.value[11],
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(end = 85.dp)
                        .clockTextExtras(text = currentClockRes.value[11])
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.width(210.dp)
                    .padding(top = 15.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.task_editor_time_picker_type_hour),
                    style = MaterialTheme.typography.subtitle2,
                    color = if (currentClockType.value == ClockType.HourSelection)
                        baseTextColor else baseTextColor.copy(0.5F),
                    modifier = Modifier.clickable {
                        currentClockType.value = ClockType.HourSelection
                    }
                )
                Text(
                    text = stringResource(id = R.string.task_editor_time_picker_type_minute),
                    style = MaterialTheme.typography.subtitle2,
                    color = if (currentClockType.value == ClockType.MinuteSelection)
                        baseTextColor else baseTextColor.copy(0.5F),
                    modifier = Modifier.clickable {
                        currentClockType.value = ClockType.MinuteSelection
                    }
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            TextOnlyButton(
                text = "AM",
                color = if (selectionAMPM.value == 0) {
                    colorResource(id = R.color.deep_sea)
                } else {
                    colorResource(id = R.color.button_neutral_background_color)
                },
                onClick = {
                    selectionAMPM.value = 0
                }
            )
            TextOnlyButton(
                text = "PM",
                color = if (selectionAMPM.value == 1) {
                    colorResource(id = R.color.deep_sea)
                } else {
                    colorResource(id = R.color.button_neutral_background_color)
                },
                onClick = {
                    selectionAMPM.value = 1
                }
            )
        }
    }
}