package com.compose.app.android.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardVoice
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.compose.app.android.R
import com.compose.app.android.account.FirebaseAccount
import com.compose.app.android.fragment.NotepadListView
import com.compose.app.android.fragment.TaskListView
import com.compose.app.android.model.TabLayoutItem
import com.compose.app.android.theme.ComposeTheme
import com.compose.app.android.utilities.getDefaultPreferences
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class ProductivityActivity : ComponentActivity() {

    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!FirebaseAccount().determineIfUserExists()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        } else {
            val asyncScope = CoroutineScope(Dispatchers.IO + Job())
            val synchronousScope = CoroutineScope(Dispatchers.Main + Job())
            asyncScope.launch {
                FirebaseAccount().sendProfileImageToFile(this@ProductivityActivity)
                synchronousScope.launch {
                    setContent {
                        MainContent()
                    }
                }
            }
        }
    }

    @Composable
    @ExperimentalPagerApi
    fun MainContent() {

        val tabLayoutItems = listOf(
            TabLayoutItem(stringResource(id = R.string.productivity_tab_item_notes)) { NotepadListView() },
            TabLayoutItem(stringResource(id = R.string.productivity_tab_item_tasks)) { TaskListView() }
        )

        val scaffoldState = rememberScaffoldState()
        val viewPagerState = rememberPagerState(pageCount = tabLayoutItems.size)
        val asyncScope = rememberCoroutineScope()

        val taskSelectedState = remember { mutableStateOf(false) }
        val noteSelectedState = remember { mutableStateOf(true) }

        val searchFieldValue = remember { mutableStateOf(TextFieldValue()) }

        val preferences = this.getDefaultPreferences()

        fun changeCurrentPageState(notePage: Boolean, taskPage: Boolean) {
            noteSelectedState.value = notePage
            taskSelectedState.value = taskPage
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
                                            text = resources.getStringArray(R.array.inspirational_quotes_default)
                                                .random(),
                                            style = MaterialTheme.typography.body1
                                        )
                                    }
                                    Image(
                                        bitmap = BitmapFactory.decodeFile("$filesDir/avatar.png")
                                            .asImageBitmap(),
                                        contentDescription = stringResource(id = R.string.avatar_icon_content_desc),
                                        modifier = Modifier
                                            .size(60.dp)
                                            .align(Alignment.CenterVertically)
                                            .padding(end = 21.dp)
                                            .clickable {

                                            }
                                    )
                                }
                                TextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 18.dp, end = 20.dp, top = 10.dp)
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
                                            imageVector = Icons.Rounded.Search,
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
                                                imageVector = Icons.Rounded.KeyboardVoice,
                                                contentDescription = stringResource(id = R.string.keyboard_voice_icon_content_desc),
                                                tint = MaterialTheme.colors.onBackground
                                            )
                                        }
                                    }
                                )
                            }
                            HorizontalPager(state = viewPagerState) {
                                tabLayoutItems[this.currentPage].tabContent()
                                when (this.currentPage) {
                                    0 -> {
                                        noteSelectedState.value = true
                                        taskSelectedState.value = false
                                    }
                                    1 -> {
                                        taskSelectedState.value = true
                                        noteSelectedState.value = false
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colorResource(id = R.color.button_neutral_background_color)),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(
                                onClick = {
                                    changeCurrentPageState(notePage = true, taskPage = false)
                                },
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    bottom = 10.dp,
                                    end = 20.dp
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                                    modifier = Modifier.size(30.dp),
                                    tint = getIconColor(state = noteSelectedState)
                                )
                            }
                            FloatingActionButton(
                                onClick = {
                                    // TODO
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(50.dp),
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = stringResource(id = R.string.add_button_content_desc)
                                )
                            }
                            IconButton(
                                onClick = {
                                    changeCurrentPageState(notePage = false, taskPage = true)
                                },
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    bottom = 10.dp,
                                    start = 20.dp
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                                    modifier = Modifier.size(30.dp),
                                    tint = getIconColor(state = taskSelectedState)
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun getIconColor(state: MutableState<Boolean>) : androidx.compose.ui.graphics.Color {
        return if (state.value) {
            colorResource(id = R.color.text_color_enabled)
        } else {
            colorResource(id = R.color.text_color_disabled)
        }
    }

}

@Preview(showBackground = true)
@Composable
@ExperimentalPagerApi
fun ProductivityPreview() {
    ProductivityActivity().MainContent()
}