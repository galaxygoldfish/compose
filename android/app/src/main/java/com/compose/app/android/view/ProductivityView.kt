package com.compose.app.android.view

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.AddNoteTaskMenuFAB
import com.compose.app.android.model.ExpandableFABItem
import com.compose.app.android.model.ExpandableFABState
import com.compose.app.android.model.NoteDocument
import com.compose.app.android.model.TaskDocument
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.theme.IconAddNew
import com.compose.app.android.theme.IconCheckCircle
import com.compose.app.android.theme.IconEditPen
import com.compose.app.android.theme.IconKeyboardVoice
import com.compose.app.android.theme.IconSearch
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.viewmodel.ProductivityViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import java.util.UUID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
fun ProductivityView(
    context: Context,
    viewModel: ProductivityViewModel,
    navController: NavController
) {

    viewModel.apply {
        updateToNewestAvatar(context.filesDir.path)
        updateNoteList()
        updateTaskList()
    }

    val asyncScope = rememberCoroutineScope()
    val preferences = context.getDefaultPreferences()

    val noteRefreshState = viewModel.isUpdatingNoteList
    val taskRefreshState = viewModel.isUpdatingTaskList

    val scaffoldState = rememberScaffoldState()
    val viewPagerState = rememberPagerState(
        pageCount = 2,
        initialPage = 0,
        initialOffscreenLimit = 2,
        infiniteLoop = false
    )
    val floatingActionState = remember { mutableStateOf(ExpandableFABState.COLLAPSED) }
    val taskSelectedState = remember { mutableStateOf(false) }
    val noteSelectedState = remember { mutableStateOf(true) }
    val searchFieldValue = remember { mutableStateOf(TextFieldValue()) }

    fun changeCurrentPageState(notePage: Boolean) {
        asyncScope.launch {
            viewPagerState.animateScrollToPage(if (notePage) 0 else 1)
        }
    }

    ComposeTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.fillMaxSize(),
            content = @Composable {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 15.dp, bottom = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(start = 20.dp)
                                        .align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = """${stringResource(id = R.string.productivity_welcome_message)} ${
                                            preferences.getString(
                                                "IDENTITY_USER_NAME_FIRST",
                                                "Error"
                                            )
                                        }""".trimMargin(),
                                        style = MaterialTheme.typography.h4,
                                    )
                                    Text(
                                        text = remember {
                                            context.resources.getStringArray(R.array.inspirational_quotes_default)
                                                .random()
                                        },
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                                BitmapFactory.decodeFile("${context.filesDir}/avatar.png")?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                                        modifier = Modifier
                                            .size(60.dp)
                                            .align(Alignment.CenterVertically)
                                            .padding(end = 16.dp)
                                            .clickable {
                                                // TODO - Show account context menu dialog
                                            }
                                    )
                                }
                            }
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 14.dp, end = 15.dp, top = 10.dp)
                                    .height(55.dp),
                                value = searchFieldValue.value,
                                placeholder = @Composable {
                                    Text(
                                        text = "Search notes & tasks",
                                        style = MaterialTheme.typography.body1,
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = colorResource(id = R.color.button_neutral_background_color),
                                    cursorColor = Color.Black,
                                    disabledLabelColor = colorResource(id = R.color.button_neutral_background_color),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                onValueChange = {
                                    searchFieldValue.value = it
                                },
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                leadingIcon = @Composable {
                                    Icon(
                                        painter = painterResource(id = IconSearch),
                                        contentDescription = stringResource(id = R.string.search_icon_content_desc),
                                        tint = MaterialTheme.colors.onBackground
                                    )
                                },
                                trailingIcon = @Composable {
                                    IconButton(
                                        onClick = {

                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = IconKeyboardVoice),
                                            contentDescription = stringResource(id = R.string.keyboard_voice_icon_content_desc),
                                            tint = MaterialTheme.colors.onBackground
                                        )
                                    }
                                }
                            )
                        }
                        HorizontalPager(
                            state = viewPagerState,
                            modifier = Modifier
                                .weight(1F)
                                .fillMaxSize()
                        ) { page ->
                            when (page) {
                                0 -> {
                                    SwipeRefresh(
                                        state = noteRefreshState,
                                        onRefresh = {
                                            viewModel.updateNoteList()
                                        },
                                        modifier = Modifier.fillMaxSize(),
                                        content = @Composable {
                                            NoteListView(
                                                noteItemList = viewModel.noteLiveList as MutableLiveData<MutableList<NoteDocument>>,
                                                context = context,
                                                onItemClick = { note ->
                                                    navController.navigate("""${NavigationDestination.NoteEditorActivity}/${note.noteID}""")
                                                }
                                            )
                                        }
                                    )
                                }
                                1 -> {
                                    SwipeRefresh(
                                        state = taskRefreshState,
                                        onRefresh = {
                                            viewModel.updateTaskList()
                                        },
                                        modifier = Modifier.fillMaxSize(),
                                        content = @Composable {
                                            TaskListView(
                                                context = context,
                                                taskList = viewModel.taskLiveList as MutableLiveData<MutableList<TaskDocument>>,
                                                onItemClick = {
                                                    navController.navigate(NavigationDestination.TaskEditorActivity)
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorResource(id = R.color.button_neutral_background_color)),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = {
                                changeCurrentPageState(notePage = true)
                            },
                            modifier = Modifier.padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                end = 20.dp
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = IconEditPen),
                                contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                                modifier = Modifier.size(30.dp),
                                tint = viewModel.getIconColor(state = noteSelectedState)
                            )
                        }
                        Spacer(modifier = Modifier.size(50.dp))
                        IconButton(
                            onClick = {
                                changeCurrentPageState(notePage = false)
                            },
                            modifier = Modifier.padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                start = 20.dp
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = IconCheckCircle),
                                contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                                modifier = Modifier.size(30.dp),
                                tint = viewModel.getIconColor(state = taskSelectedState)
                            )
                        }
                        LaunchedEffect(viewPagerState) {
                            snapshotFlow { viewPagerState.currentPage }.collect {
                                taskSelectedState.value = it != 0
                                noteSelectedState.value = it == 0
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 22.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AddNoteTaskMenuFAB(
                            icon = painterResource(id = IconAddNew),
                            contentDescription = stringResource(id = R.string.add_button_content_desc),
                            expandedState = floatingActionState.value,
                            modifier = Modifier.size(45.dp),
                            onExpansion = { state ->
                                floatingActionState.value = state
                            },
                            menuItems = listOf(
                                ExpandableFABItem(
                                    icon = painterResource(id = IconEditPen),
                                    contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                                    label = stringResource(id = R.string.productivity_menu_notes),
                                    onClick = {
                                        navController.navigate("""${NavigationDestination.NoteEditorActivity}/${UUID.randomUUID()}""")
                                    }
                                ),
                                ExpandableFABItem(
                                    icon = painterResource(id = IconCheckCircle),
                                    contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                                    label = stringResource(id = R.string.productivity_menu_task),
                                    onClick = {
                                        navController.navigate(NavigationDestination.TaskEditorActivity)
                                    }
                                ),
                            )
                        )
                    }
                }
            }
        )
    }
}