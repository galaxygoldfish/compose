package com.compose.app.android.view

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.AddNoteTaskMenuFAB
import com.compose.app.android.components.OptionListItem
import com.compose.app.android.model.ExpandableFAB
import com.compose.app.android.model.ExpandableFABState
import com.compose.app.android.model.NoteDocument
import com.compose.app.android.model.TaskDocument
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.*
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.viewmodel.ProductivityViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalAnimationApi
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

    val preferences = context.getDefaultPreferences()
    val composeAsync = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val viewPagerState = rememberPagerState(
        pageCount = 2,
        initialPage = 0,
        initialOffscreenLimit = 2,
        infiniteLoop = false
    )

    // todo - move these to the viewModel

    val floatingActionState = remember { mutableStateOf(ExpandableFABState.COLLAPSED) }
    val taskSelectedState = remember { mutableStateOf(false) }
    val noteSelectedState = remember { mutableStateOf(true) }
    val searchFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val showProfileContextDialog = remember { mutableStateOf(false) }

    fun changeCurrentPageState(notePage: Boolean) {
        composeAsync.launch {
            viewPagerState.animateScrollToPage(if (notePage) 0 else 1)
        }
    }

    (context as ComposeBaseActivity).apply {
        if (bottomSheetState.isVisible) {
            window.navigationBarColor = resources.getColor(R.color.neutral_gray)
        } else {
            window.navigationBarColor = resources.getColor(R.color.text_color_reverse)
        }
    }

    ComposeTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.fillMaxSize(),
            content = @Composable {
                ModalBottomSheetLayout(
                    sheetState = bottomSheetState,
                    sheetContent = {
                        Box {
                            when (viewPagerState.currentPage) {
                                0 -> NoteOptionMenu(viewModel, navController, context, bottomSheetState)
                                1 -> TaskOptionMenu(viewModel, navController, bottomSheetState, context)
                            }
                        }
                    },
                    sheetShape = RoundedCornerShape(8.dp),
                    sheetElevation = 20.dp,
                    sheetBackgroundColor = colorResource(id = R.color.neutral_gray),
                    scrimColor = MaterialTheme.colors.surface.copy(0.5F)
                ) {
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
                                    BitmapFactory.decodeFile("${context.filesDir}/avatar.png")
                                        ?.let {
                                            Image(
                                                bitmap = it.asImageBitmap(),
                                                contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                                                modifier = Modifier
                                                    .padding(end = 16.dp)
                                                    .clip(CircleShape)
                                                    .size(40.dp)
                                                    .aspectRatio(1F)
                                                    .align(Alignment.CenterVertically)
                                                    .clickable {
                                                        showProfileContextDialog.value = true
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
                                        backgroundColor = colorResource(id = R.color.neutral_gray),
                                        cursorColor = Color.Black,
                                        disabledLabelColor = colorResource(id = R.color.neutral_gray),
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
                                            state = viewModel.isUpdatingNoteList,
                                            onRefresh = {
                                                viewModel.updateNoteList()
                                            },
                                            modifier = Modifier.fillMaxSize(),
                                            content = @Composable {
                                                ExperimentalNoteListView(
                                                    noteItemList = viewModel.noteLiveList as MutableLiveData<MutableList<NoteDocument>>,
                                                    context = context,
                                                    onItemLongClick = { note ->
                                                        viewModel.apply {
                                                            bottomSheetNoteDocument.value = note
                                                            composeAsync.launch {
                                                                bottomSheetState.show()
                                                            }
                                                        }
                                                    },
                                                    onItemClick = { note ->
                                                        navController.navigate("""${NavigationDestination.NoteEditorActivity}/${note.noteID}""")
                                                    }
                                                )
                                            }
                                        )
                                    }
                                    1 -> {
                                        SwipeRefresh(
                                            state = viewModel.isUpdatingTaskList,
                                            onRefresh = {
                                                viewModel.updateTaskList()
                                            },
                                            modifier = Modifier.fillMaxSize(),
                                            content = @Composable {
                                                TaskListView(
                                                    context = context,
                                                    taskList = viewModel.taskLiveList as MutableLiveData<MutableList<TaskDocument>>,
                                                    onItemClick = { task ->
                                                        navController.navigate("""${NavigationDestination.TaskEditorActivity}/${task.taskID}""")
                                                    },
                                                    onItemLongClick = { task ->
                                                        viewModel.apply {
                                                            bottomSheetTaskDocument.value = task
                                                            composeAsync.launch {
                                                                bottomSheetState.show()
                                                            }
                                                        }
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
                                .background(colorResource(id = R.color.neutral_gray)),
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
                                    ExpandableFAB(
                                        icon = painterResource(id = IconEditPen),
                                        contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                                        label = stringResource(id = R.string.productivity_menu_notes),
                                        onClick = {
                                            navController.navigate("""${NavigationDestination.NoteEditorActivity}/${UUID.randomUUID()}""")
                                        }
                                    ),
                                    ExpandableFAB(
                                        icon = painterResource(id = IconCheckCircle),
                                        contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                                        label = stringResource(id = R.string.productivity_menu_task),
                                        onClick = {
                                            navController.navigate("""${NavigationDestination.TaskEditorActivity}/${UUID.randomUUID()}""")
                                        }
                                    ),
                                )
                            )
                        }
                        if (showProfileContextDialog.value) {
                            val dataStore = navController.context.getDefaultPreferences()
                            Dialog(
                                onDismissRequest = {
                                },
                                properties = DialogProperties(
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = true
                                ),
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(colorResource(id = R.color.neutral_gray))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            BitmapFactory.decodeFile(
                                                "${context.filesDir}/avatar.png"
                                            )?.let {
                                                    Image(
                                                        bitmap = it.asImageBitmap(),
                                                        contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                                                        modifier = Modifier
                                                            .padding(start = 20.dp, top = 20.dp)
                                                            .clip(CircleShape)
                                                            .size(90.dp)
                                                            .aspectRatio(1F)
                                                            .align(Alignment.CenterVertically)
                                                    )
                                                }
                                            Column(
                                                modifier = Modifier
                                                    .padding(top = 30.dp, start = 25.dp)
                                            ) {
                                                Text(
                                                    text = dataStore.getString("IDENTITY_USER_NAME_FIRST", "Error")!!,
                                                    style = MaterialTheme.typography.h4,
                                                    modifier = Modifier.padding()
                                                )
                                                Text(
                                                    text = dataStore.getString("IDENTITY_USER_NAME_LAST", "Error")!!,
                                                    style = MaterialTheme.typography.h4,
                                                    fontWeight = FontWeight.Normal,
                                                    modifier = Modifier.padding()
                                                )
                                            }
                                        }
                                        Column(
                                            modifier = Modifier.padding(top = 25.dp, bottom = 15.dp)
                                        ) {
                                            OptionListItem(
                                                icon = IconSettings,
                                                contentDescription = stringResource(id = R.string.settings_icon_content_desc),
                                                title = stringResource(id = R.string.profile_context_menu_settings_title),
                                                onClick = {

                                                }
                                            )
                                            OptionListItem(
                                                icon = IconPersonGroup,
                                                contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                                                title = stringResource(id = R.string.profile_context_menu_account_settings_title),
                                                onClick = {
                                                    
                                                }
                                            )
                                            OptionListItem(
                                                icon = IconThemeColor,
                                                contentDescription = stringResource(id = R.string.theme_switch_icon_content_desc),
                                                title = stringResource(id = R.string.profile_context_menu_switch_theme_title),
                                                onClick = {

                                                }
                                            )
                                            OptionListItem(
                                                icon = IconLogIn,
                                                contentDescription = stringResource(id = R.string.sign_out_content_desc),
                                                title = stringResource(id = R.string.profile_context_menu_sign_out_title),
                                                onClick = {

                                                }
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}