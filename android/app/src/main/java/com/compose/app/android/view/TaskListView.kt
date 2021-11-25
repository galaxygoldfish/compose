package com.compose.app.android.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.TaskDocument
import com.compose.app.android.theme.IconCalendar
import com.compose.app.android.theme.IconNotification
import com.compose.app.android.viewmodel.ProductivityViewModel
import kotlinx.coroutines.delay

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
@ExperimentalMaterialApi
fun TaskListView(
    viewModel: ProductivityViewModel,
    onItemClick: (TaskDocument) -> Unit,
    onItemLongClick: (TaskDocument) -> Unit
) {
    val taskListState = rememberLazyListState()
    val taskItemState = viewModel.taskLiveList.observeAsState().value
    val cardVisibility = remember { mutableStateOf(false) }

    if (taskItemState!!.isEmpty()) {
        cardVisibility.value = false
    }

    if (taskItemState.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp, start = 14.dp, end = 15.dp),
            state = taskListState,
            content = {
                items(
                    count = taskItemState.size,
                    itemContent = { index ->
                        TaskListCard(
                            item = taskItemState[index],
                            onClick = onItemClick,
                            onItemLongClick = onItemLongClick,
                            itemVisibility = cardVisibility,
                            index = index
                        )
                    }
                )
            }
        )
        LaunchedEffect(key1 = true) {
            delay(200L)
            cardVisibility.value = true
        }
    } else {
        if (!viewModel.isUpdatingTaskList.isRefreshing) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.2F))
                Image(
                    painter = painterResource(id = R.drawable.ic_nothing_here_illustration),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 100.dp, end = 100.dp)
                )
                Text(
                    text = stringResource(id = R.string.productivity_nothing_here_message),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 15.dp)
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.3F))
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
@ExperimentalMaterialApi
fun TaskListCard(
    item: TaskDocument,
    index: Int,
    onClick: (TaskDocument) -> Unit,
    onItemLongClick: (TaskDocument) -> Unit,
    itemVisibility: MutableState<Boolean>
) {
    val taskCheckboxState = remember { mutableStateOf(item.isComplete) }

    /**
     * If the task is complete, cross off the text and dim it's
     * color, otherwise fall back to the default style.
     */
    @Composable
    fun getVariableTextStyle(defaultStyle: TextStyle): TextStyle {
        return if (taskCheckboxState.value) {
            defaultStyle.plus(
                TextStyle(
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colors.onBackground.copy(0.5F)
                )
            )
        } else {
            defaultStyle
        }
    }

    AnimatedVisibility(visible = itemVisibility.value) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 9.dp)
                .combinedClickable(
                    onLongClick = {
                        onItemLongClick.invoke(item)
                    },
                    onClick = {
                        onClick.invoke(item)
                    }
                )
                .animateEnterExit(
                    enter = slideInHorizontally(
                        animationSpec = spring(dampingRatio = 0.9F),
                        initialOffsetX = {
                            it * (index + 1)
                        }
                    )
                ),
            shape = RoundedCornerShape(7.dp),
            backgroundColor = MaterialTheme.colors.primaryVariant,
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Checkbox(
                    checked = taskCheckboxState.value,
                    onCheckedChange = { checked ->
                        FirebaseDocument().updateTaskCompletion(checked, item.taskID)
                        taskCheckboxState.value = checked
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            maxLines = 2,
                            text = item.taskTitle,
                            style = getVariableTextStyle(defaultStyle = MaterialTheme.typography.h6),
                            fontSize = 17.sp,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(top = 6.dp, end = 15.dp, start = 10.dp),
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, bottom = 7.dp, start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = IconCalendar),
                            contentDescription = stringResource(id = R.string.calendar_icon_content_desc),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colors.onBackground.copy(0.7F)
                        )
                        Text(
                            text = item.dueDate.split(",")[0],
                            style = getVariableTextStyle(defaultStyle = MaterialTheme.typography.body1),
                            fontSize = 15.sp,
                            color = MaterialTheme.colors.onBackground.copy(0.7F),
                            modifier = Modifier.padding(start = 5.dp, end = 15.dp)
                        )
                        Icon(
                            painter = painterResource(id = IconNotification),
                            contentDescription = stringResource(id = R.string.notification_bell_content_desc),
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colors.onBackground.copy(0.7F)
                        )
                        Text(
                            text = item.dueTime,
                            style = getVariableTextStyle(defaultStyle = MaterialTheme.typography.body1),
                            fontSize = 15.sp,
                            color = MaterialTheme.colors.onBackground.copy(0.7F),
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }
            }
        }
    }
}