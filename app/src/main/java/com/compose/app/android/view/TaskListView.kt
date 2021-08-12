package com.compose.app.android.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.TaskDocument

@Composable
@ExperimentalMaterialApi
fun TaskListView(
    context: Context,
    onItemClick: () -> Unit,
    taskList: MutableLiveData<MutableList<TaskDocument>>
) {
    val taskListState = rememberLazyListState()
    val taskItemState = taskList.observeAsState().value!!
    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .padding(top = 15.dp, start = 14.dp, end = 15.dp),
        state = taskListState,
        content = {
            items(
                count = taskItemState.size,
                itemContent = @Composable { index ->
                    TaskListCard(
                        index = index,
                        onClick = onItemClick,
                        taskList = taskItemState
                    )
                }
            )
        }
    )
}

@Composable
@ExperimentalMaterialApi
fun TaskListCard(
    index: Int,
    onClick: () -> Unit,
    taskList: MutableList<TaskDocument>
) {
    val currentTask = taskList[index]
    val taskCheckboxState = remember { mutableStateOf(currentTask.isComplete) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 9.dp),
        shape = RoundedCornerShape(7.dp),
        backgroundColor = colorResource(id = R.color.button_neutral_background_color),
        elevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(start = 15.dp)
        ) {
            Checkbox(
                checked = taskCheckboxState.value,
                onCheckedChange = { checked ->
                    taskCheckboxState.value = checked
                    FirebaseDocument().updateTaskCompletion(checked, currentTask.taskID)
                },
                modifier = Modifier
                    .padding(end = 10.dp)
                    .align(Alignment.CenterVertically)
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
                        text = currentTask.taskTitle,
                        style = MaterialTheme.typography.h6,
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
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = stringResource(id = R.string.calendar_icon_content_desc),
                        modifier = Modifier.size(16.dp),
                        tint = colorResource(id = R.color.text_color_disabled)
                    )
                    Text(
                        text = currentTask.dueDate,
                        style = MaterialTheme.typography.body1,
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.text_color_disabled),
                        modifier = Modifier.padding(start = 5.dp, end = 15.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = stringResource(id = R.string.notification_bell_content_desc),
                        modifier = Modifier.size(16.dp),
                        tint = colorResource(id = R.color.text_color_disabled)
                    )
                    Text(
                        text = currentTask.dueTime,
                        style = MaterialTheme.typography.body1,
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.text_color_disabled),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
    }
}