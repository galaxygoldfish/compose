package com.compose.app.android.view

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.TaskDocument
import com.compose.app.android.theme.IconCalendar
import com.compose.app.android.theme.IconNotification

@ExperimentalFoundationApi
@Composable
@ExperimentalMaterialApi
fun TaskListView(
    context: Context,
    onItemClick: (TaskDocument) -> Unit,
    onItemLongClick: (TaskDocument) -> Unit,
    taskList: MutableLiveData<MutableList<TaskDocument>>
) {
    val taskListState = rememberLazyListState()
    val taskItemState = taskList.observeAsState().value!!
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 15.dp, start = 14.dp, end = 15.dp),
        state = taskListState,
        content = {
            items(
                count = taskItemState.size,
                itemContent = @Composable { index ->
                    TaskListCard(
                        item = taskItemState[index],
                        onClick = onItemClick,
                        onItemLongClick = onItemLongClick
                    )
                }
            )
        }
    )
}

@ExperimentalFoundationApi
@Composable
@ExperimentalMaterialApi
fun TaskListCard(
    item: TaskDocument,
    onClick: (TaskDocument) -> Unit,
    onItemLongClick: (TaskDocument) -> Unit
) {
    val taskCheckboxState = remember { mutableStateOf(item.isComplete) }
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
            ),
        shape = RoundedCornerShape(7.dp),
        backgroundColor = colorResource(id = R.color.neutral_gray),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 15.dp)
        ) {
            Checkbox(
                checked = taskCheckboxState.value,
                onCheckedChange = { checked ->
                    FirebaseDocument().updateTaskCompletion(checked, item.taskID)
                    taskCheckboxState.value = checked
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
                        text = item.taskTitle,
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
                        painter = painterResource(id = IconCalendar),
                        contentDescription = stringResource(id = R.string.calendar_icon_content_desc),
                        modifier = Modifier.size(16.dp),
                        tint = colorResource(id = R.color.text_color_disabled)
                    )
                    Text(
                        text = item.dueDate.split(",")[0],
                        style = MaterialTheme.typography.body1,
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.text_color_disabled),
                        modifier = Modifier.padding(start = 5.dp, end = 15.dp)
                    )
                    Icon(
                        painter = painterResource(id = IconNotification),
                        contentDescription = stringResource(id = R.string.notification_bell_content_desc),
                        modifier = Modifier.size(16.dp),
                        tint = colorResource(id = R.color.text_color_disabled)
                    )
                    Text(
                        text = item.dueTime,
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