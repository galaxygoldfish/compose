package com.compose.app.android.view

import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import com.compose.app.android.viewmodel.TaskEditorViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.time.YearMonth

// TODO: Fix calendar grid so that each day is displayed under correct weekday

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun DatePickerSheetView(
    viewModel: TaskEditorViewModel
) {

    val monthArrayResource = stringArrayResource(id = R.array.month_list)
    val weekdays = stringArrayResource(id = R.array.weekday_list_abbreviated)


    val composeAsync = rememberCoroutineScope()
    val textColorDefault =  MaterialTheme.colors.onBackground

    val viewPagerState = rememberPagerState(pageCount = 2)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primaryVariant)
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
                    text = "${viewModel.currentMonth.value} ${viewModel.selectedDayIndex.value}, ${viewModel.currentYear.value}",
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
                    text = "${viewModel.selectedHour.value}:${viewModel.selectedMinute.value} ${if (viewModel.selectionAMPM.value == 0) "AM" else "PM"}",
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
                        if (monthArrayResource.indexOf(viewModel.currentMonth.value) == 0) {
                            viewModel.currentMonth.value = monthArrayResource[11]
                            viewModel.currentYear.value = (viewModel.currentYear.value.toInt() - 1).toString()
                        } else {
                            val oldMonth = viewModel.currentMonth.value
                            viewModel.currentMonth.value = monthArrayResource[monthArrayResource.indexOf(oldMonth) - 1]
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
                        if (monthArrayResource.indexOf(viewModel.currentMonth.value) == 11) {
                            val oldYearValue = viewModel.currentYear.value.toLong()
                            viewModel.currentMonth.value = monthArrayResource[0]
                            viewModel.currentYear.value = (oldYearValue + 1).toString()
                        } else {
                            viewModel.currentMonth.value = monthArrayResource[monthArrayResource.indexOf(viewModel.currentMonth.value) + 1]
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
                    viewModel = viewModel
                )
                1 -> TimeHourPicker(
                    viewModel = viewModel
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun CalendarDayPicker(
    weekdayStringArray: Array<String>,
    viewModel: TaskEditorViewModel
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
                                        color = MaterialTheme.colors.onBackground.copy(0.7F),
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            items(
                                YearMonth.of(
                                    viewModel.currentYear.value.replace(" ", "").toInt(),
                                    viewModel.monthIndex.value
                                ).lengthOfMonth()
                            ) { index ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(45.dp)
                                        .padding(3.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (viewModel.selectedDayIndex.value == index + 1) {
                                                colorResource(id = R.color.deep_sea)
                                            } else {
                                                MaterialTheme.colors.primaryVariant
                                            }
                                        )
                                        .clickable {
                                            viewModel.selectedDayIndex.value = index + 1
                                            viewModel.interactionMonitor.value = true
                                        }
                                ) {
                                    Text(
                                        text = (index + 1).toString(),
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun TimeHourPicker(
    viewModel: TaskEditorViewModel
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
                    MaterialTheme.colors.primary
                }
            )
            .clickable {
                viewModel.interactionMonitor.value = true
                asyncScope.launch {
                    val editedText = text.replace(" ", "")
                    when (currentClockType.value) {
                        ClockType.HourSelection -> {
                            viewModel.selectedHour.value = editedText
                        }
                        ClockType.MinuteSelection -> {
                            viewModel.selectedMinute.value = editedText
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
                            if (!MaterialTheme.colors.isLight) 0.3F else 0.9F
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
                modifier = Modifier
                    .width(210.dp)
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
                color = if (viewModel.selectionAMPM.value == 0) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.secondaryVariant
                },
                onClick = {
                    viewModel.selectionAMPM.value = 0
                }
            )
            TextOnlyButton(
                text = "PM",
                color = if (viewModel.selectionAMPM.value == 1) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.secondaryVariant
                },
                onClick = {
                    viewModel.selectionAMPM.value = 1
                }
            )
        }
    }
}