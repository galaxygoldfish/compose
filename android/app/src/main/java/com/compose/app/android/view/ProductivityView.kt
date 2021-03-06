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

import android.content.Intent
import android.text.format.Formatter.formatFileSize
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.compose.app.android.BuildConfig
import com.compose.app.android.R
import com.compose.app.android.components.AddNoteTaskMenuFAB
import com.compose.app.android.components.FullWidthButton
import com.compose.app.android.components.OptionListItem
import com.compose.app.android.model.ExpandableFAB
import com.compose.app.android.presentation.ComposeBaseActivity
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.*
import com.compose.app.android.utilities.createSquareImage
import com.compose.app.android.utilities.getCloudPreferences
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.utilities.getViewModel
import com.compose.app.android.view.settings.LogOutAccountDialog
import com.compose.app.android.viewmodel.ProductivityViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun ProductivityView(navController: NavController) {

    val viewModel = navController.context.getViewModel(ProductivityViewModel::class.java)

    viewModel.apply {
        updateToNewestAvatar(navController.context.filesDir.path)
        updateNoteList()
        updateTaskList()
        updateStorageCount()
    }

    LocalContext.current.apply {
        if (
            BuildConfig.VERSION_NAME.contains("beta") &&
            !getDefaultPreferences().getBoolean("SHOW_BETA_WELCOME", false)
        ) {
            viewModel.showingBetaProgramDialog.value = true
        }
    }

    val scaffoldState = rememberScaffoldState()
    val systemUiController = rememberSystemUiController()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val viewPagerState = rememberPagerState(
        pageCount = 2,
        initialPage = 0,
        initialOffscreenLimit = 2,
        infiniteLoop = false
    )

    if (bottomSheetState.isVisible) {
        systemUiController.setNavigationBarColor(
            color = MaterialTheme.colors.primaryVariant
        )
    } else {
        systemUiController.setNavigationBarColor(
            color = MaterialTheme.colors.background
        )
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
                                0 -> NoteOptionMenu(
                                    navController = navController,
                                    bottomSheetState = bottomSheetState
                                )
                                1 -> TaskOptionMenu(
                                    navController = navController,
                                    bottomSheetState = bottomSheetState
                                )
                            }
                        }
                    },
                    sheetShape = RoundedCornerShape(8.dp),
                    sheetElevation = 20.dp,
                    sheetBackgroundColor = MaterialTheme.colors.primaryVariant,
                    scrimColor = MaterialTheme.colors.surface.copy(0.5F)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            TopAppBar(
                                navController = navController,
                                viewModel = viewModel
                            )
                            SearchBar(
                                viewModel = viewModel,
                                navController = navController
                            )
                            NoteTaskPager(
                                viewModel = viewModel,
                                viewPagerState = viewPagerState,
                                navController = navController,
                                bottomSheetState = bottomSheetState
                            )
                        }
                        BottomNavigationBar(
                            viewModel = viewModel,
                            viewPagerState = viewPagerState,
                            navController = navController,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                        ProfileContextMenu(
                            navController = navController,
                            viewModel = viewModel
                        )
                        LogOutAccountDialog(
                            navController = navController,
                            showingDialog = viewModel.showingLogOutDialog
                        )
                        BetaProgramDialog(viewModel = viewModel)
                    }
                }
            }
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun ProfileContextMenu(
    navController: NavController,
    viewModel: ProductivityViewModel
) {
    AnimatedVisibility(
        visible = viewModel.showProfileContextDialog.value
    ) {
        val dataStore = navController.context.getDefaultPreferences()
        Dialog(
            onDismissRequest = {
                viewModel.showProfileContextDialog.value = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            content = {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colors.primaryVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(top = 30.dp, start = 25.dp)
                        ) {
                            Text(
                                text = dataStore.getString(
                                    "IDENTITY_USER_NAME_FIRST",
                                    "Error"
                                )!!,
                                style = MaterialTheme.typography.h4,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding()
                            )
                            Text(
                                text = dataStore.getString(
                                    "IDENTITY_USER_NAME_LAST",
                                    "Error"
                                )!!,
                                style = MaterialTheme.typography.h4,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding()
                            )
                            Text(
                                text = String.format(
                                    stringResource(id = R.string.profile_context_menu_storage_template),
                                    formatFileSize(
                                        navController.context,
                                        viewModel.userStorageSize.value.toLong()
                                    )
                                ),
                                fontSize = 13.sp,
                                color = MaterialTheme.colors.onBackground.copy(0.7F)
                            )
                        }
                            Image(
                                bitmap = viewModel.avatarImageStore.value?.asImageBitmap().let {
                                    it ?: ImageBitmap.imageResource(id = IconAccountCircle)
                                },
                                contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                                modifier = Modifier
                                    .padding(end = 20.dp, top = 20.dp)
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.CenterVertically)
                            )
                    }
                    Column(
                        modifier = Modifier.padding(top = 25.dp, bottom = 15.dp)
                    ) {
                        OptionListItem(
                            icon = IconSettings,
                            contentDescription = stringResource(id = R.string.settings_icon_content_desc),
                            title = stringResource(id = R.string.profile_context_menu_settings_title),
                            onClick = {
                                viewModel.showProfileContextDialog.value = false
                                navController.navigate(NavigationDestination.SettingsViewHome)
                            }
                        )
                        OptionListItem(
                            icon = IconPersonGroup,
                            contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                            title = stringResource(id = R.string.profile_context_menu_account_settings_title),
                            onClick = {
                                navController.navigate(NavigationDestination.AccountSettings)
                            }
                        )
                        OptionListItem(
                            icon = IconThemeColor,
                            contentDescription = stringResource(id = R.string.theme_switch_icon_content_desc),
                            title = stringResource(id = R.string.profile_context_menu_switch_theme_title),
                            onClick = {
                                currentAppThemeState.value = !currentAppThemeState.value
                                navController.apply {
                                    context.getCloudPreferences().apply {
                                        putBoolean("STATE_DARK_MODE", currentAppThemeState.value)
                                    }
                                    context.startActivity(
                                        Intent(navController.context, ComposeBaseActivity::class.java)
                                    )
                                }
                            }
                        )
                        OptionListItem(
                            icon = IconLogIn,
                            contentDescription = stringResource(id = R.string.sign_out_content_desc),
                            title = stringResource(id = R.string.profile_context_menu_sign_out_title),
                            onClick = {
                                viewModel.apply {
                                    showProfileContextDialog.value = false
                                    showingLogOutDialog.value = true
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun SearchBar(
    viewModel: ProductivityViewModel,
    navController: NavController
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 15.dp, top = 10.dp)
            .height(55.dp),
        value = viewModel.searchFieldValue.value,
        placeholder = @Composable {
            Text(
                text = "Search notes & tasks",
                style = MaterialTheme.typography.body1,
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.primaryVariant,
            cursorColor = Color.Black,
            disabledLabelColor = MaterialTheme.colors.primaryVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = {
            viewModel.searchFieldValue.value = it
            when (viewModel.noteSelectedState.value) {
                true -> viewModel.updateNoteList(it.text)
                false -> viewModel.updateTaskList(it.text)
            }

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

@Composable
fun TopAppBar(
    navController: NavController,
    viewModel: ProductivityViewModel
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
                        navController.context.getDefaultPreferences()
                            .getString(
                                "IDENTITY_USER_NAME_FIRST",
                                "Error"
                            )
                    }""".trimMargin(),
                    style = MaterialTheme.typography.h4,
                )
                Text(
                    text = remember {
                        navController.context.resources.getStringArray(R.array.inspirational_quotes_default)
                            .random()
                    },
                    style = MaterialTheme.typography.body1
                )
            }
            viewModel.avatarImageStore.value?.let {
                Image(
                    bitmap = it.createSquareImage().asImageBitmap(),
                    contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            viewModel.showProfileContextDialog.value = true
                        }
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun NoteTaskPager(
    viewModel: ProductivityViewModel,
    viewPagerState: PagerState,
    navController: NavController,
    bottomSheetState: ModalBottomSheetState
) {
    val composeAsync = rememberCoroutineScope()
    HorizontalPager(
        state = viewPagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> {
                SwipeRefresh(
                    state = viewModel.isUpdatingNoteList,
                    onRefresh = {
                        viewModel.apply {
                            noteLiveList.value = mutableListOf()
                            updateNoteList(null)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    content = @Composable {
                        ExperimentalNoteListView(
                            viewModel = viewModel,
                            onItemLongClick = { note ->
                                viewModel.apply {
                                    bottomSheetNoteDocument.value = note
                                    composeAsync.launch {
                                        bottomSheetState.show()
                                    }
                                }
                            },
                            onItemClick = { note ->
                                navController.navigate("""${NavigationDestination.NoteEditorView}/${note.noteID}""")
                            }
                        )
                    }
                )
            }
            1 -> {
                SwipeRefresh(
                    state = viewModel.isUpdatingTaskList,
                    onRefresh = {
                        viewModel.apply {
                            taskLiveList.value = mutableListOf()
                            updateTaskList()
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    content = @Composable {
                        TaskListView(
                            viewModel = viewModel,
                            onItemClick = { task ->
                                navController.navigate("""${NavigationDestination.TaskEditorView}/${task.taskID}""")
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

@ExperimentalPagerApi
@Composable
fun BottomNavigationBar(
    viewModel: ProductivityViewModel,
    viewPagerState: PagerState,
    navController: NavController,
    modifier: Modifier
) {
    val composeAsync = rememberCoroutineScope()
    fun changeCurrentPageState(notePage: Boolean) {
        composeAsync.launch {
            viewPagerState.animateScrollToPage(if (notePage) 0 else 1)
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.primaryVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            onClick = {
                changeCurrentPageState(true)
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
                tint = viewModel.getIconColor(state = viewModel.noteSelectedState)
            )
        }
        Spacer(modifier = Modifier.size(50.dp))
        IconButton(
            onClick = {
                changeCurrentPageState(false)
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
                tint = viewModel.getIconColor(state = viewModel.taskSelectedState)
            )
        }
        LaunchedEffect(viewPagerState) {
            snapshotFlow { viewPagerState.currentPage }.collect {
                viewModel.taskSelectedState.value = it != 0
                viewModel.noteSelectedState.value = it == 0
            }
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 22.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        AddNoteTaskMenuFAB(
            icon = painterResource(id = IconAddNew),
            contentDescription = stringResource(id = R.string.add_button_content_desc),
            expandedState = viewModel.floatingActionState.value,
            modifier = Modifier.size(45.dp),
            onExpansion = { state ->
                viewModel.floatingActionState.value = state
            },
            menuItems = listOf(
                ExpandableFAB(
                    icon = painterResource(id = IconEditPen),
                    contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                    label = stringResource(id = R.string.productivity_menu_notes),
                    onClick = {
                        navController.navigate("""${NavigationDestination.NoteEditorView}/${UUID.randomUUID()}""")
                    }
                ),
                ExpandableFAB(
                    icon = painterResource(id = IconCheckCircle),
                    contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                    label = stringResource(id = R.string.productivity_menu_task),
                    onClick = {
                        navController.navigate("""${NavigationDestination.TaskEditorView}/${UUID.randomUUID()}""")
                    }
                ),
            )
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun BetaProgramDialog(viewModel: ProductivityViewModel) {
    AnimatedVisibility(
        visible = viewModel.showingBetaProgramDialog.value
    ) {
        Dialog(
            onDismissRequest = {
                viewModel.showingBetaProgramDialog.value = false
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.background)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_illustration_beta_program),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 23.dp, start = 20.dp, end = 20.dp)
                )
                Text(
                    text = stringResource(id = R.string.beta_program_dialog_header),
                    modifier = Modifier.padding(start = 20.dp, top = 15.dp),
                    style = MaterialTheme.typography.h4
                )
                Text(
                    text = stringResource(id = R.string.beta_program_dialog_body),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp)
                )
                LocalContext.current.apply {
                    FullWidthButton(
                        text = stringResource(id = R.string.beta_program_dialog_positive_button),
                        icon = painterResource(id = IconCheckMark),
                        contentDescription = "",
                        color = MaterialTheme.colors.primaryVariant,
                        contentColor = MaterialTheme.colors.onBackground,
                        textStyle = MaterialTheme.typography.body1
                    ) {
                        getDefaultPreferences().edit().putBoolean("SHOW_BETA_WELCOME", true).commit()
                        viewModel.showingBetaProgramDialog.value = false
                    }
                }
                Spacer(Modifier.padding(bottom = 15.dp))
            }
        }
    }
}