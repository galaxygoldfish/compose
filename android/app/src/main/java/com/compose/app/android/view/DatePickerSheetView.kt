/**
 * Copyright (C) 2021  Sebastian Hriscu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **/
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.NumberPicker
import com.compose.app.android.R
import com.compose.app.android.components.SheetHandle
import com.compose.app.android.theme.IconLeftArrowSmall
import com.compose.app.android.theme.IconRightArrowSmall
import com.compose.app.android.theme.currentAppAccentColor
import com.compose.app.android.viewmodel.TaskEditorViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.time.YearMonth

// TODO: Fix calendar grid so that each day is displayed under correct weekday
// TODO: Fix or make pull request in numberpicker library so it displays minutes correctly

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
    val textColorDefault = MaterialTheme.colors.onBackground

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
                    color = if (viewPagerState.currentPage == 0) {
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
                            viewModel.currentYear.value =
                                (viewModel.currentYear.value.toInt() - 1).toString()
                        } else {
                            val oldMonth = viewModel.currentMonth.value
                            viewModel.currentMonth.value =
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
                        if (monthArrayResource.indexOf(viewModel.currentMonth.value) == 11) {
                            val oldYearValue = viewModel.currentYear.value.toLong()
                            viewModel.currentMonth.value = monthArrayResource[0]
                            viewModel.currentYear.value = (oldYearValue + 1).toString()
                        } else {
                            viewModel.currentMonth.value =
                                monthArrayResource[monthArrayResource.indexOf(viewModel.currentMonth.value) + 1]
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
                                                currentAppAccentColor.value
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
                                        modifier = Modifier.align(Alignment.Center),
                                        color = MaterialTheme.colors.onBackground
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
    val colorFixedTextStyle = MaterialTheme.typography.body2.plus(
        TextStyle(color = MaterialTheme.colors.onBackground, fontSize = 30.sp)
    )
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            NumberPicker(
                value = viewModel.selectedHour.value.toInt(),
                onValueChange = {
                    viewModel.apply {
                        selectedHour.value = it.toString()
                        interactionMonitor.value = true
                    }
                },
                range = 1..12,
                dividersColor = Color.Transparent,
                textStyle = colorFixedTextStyle,
                modifier = Modifier.padding(end = 20.dp)
            )
            Text(
                text = ":",
                style = colorFixedTextStyle,
                modifier = Modifier.padding(end = 20.dp)
            )
            NumberPicker(
                value = viewModel.selectedMinute.value.toInt(),
                onValueChange = {
                    viewModel.apply {
                        selectedMinute.value = it.toString()
                        interactionMonitor.value = true
                    }
                },
                range = 0..59,
                dividersColor = Color.Transparent,
                textStyle = colorFixedTextStyle,
                modifier = Modifier.padding(end = 20.dp)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                val textColorMain = MaterialTheme.colors.onBackground
                Text(
                    text = "AM",
                    style = colorFixedTextStyle,
                    color = if (viewModel.selectionAMPM.value == 0) textColorMain else textColorMain
                        .copy(0.6F),
                    modifier = Modifier.clickable {
                        viewModel.selectionAMPM.value = 0
                    }
                )
                Text(
                    text = "PM",
                    style = colorFixedTextStyle,
                    color = if (viewModel.selectionAMPM.value == 1) textColorMain else textColorMain
                        .copy(0.6F),
                    modifier = Modifier.clickable {
                        viewModel.selectionAMPM.value = 1
                    }
                )
            }
        }
    }
}