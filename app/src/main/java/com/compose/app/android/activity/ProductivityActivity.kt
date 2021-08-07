package com.compose.app.android.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.launch

class ProductivityActivity : ComponentActivity() {

    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!FirebaseAccount().determineIfUserExists()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        setContent {
            MainContent()
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
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = """${stringResource(id = R.string.productivity_welcome_message)} ${preferences.getString("IDENTITY_USER_NAME_FIRST", "Error")}"""
                                        .trimMargin(),
                                    style = MaterialTheme.typography.h4,
                                    modifier = Modifier.padding(top = 15.dp, start = 20.dp)
                                )
                            }
                            HorizontalPager(state = viewPagerState) {
                                tabLayoutItems[this.currentPage].tabContent()
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
                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 20.dp)
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
                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 20.dp)
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
    fun getIconColor(state: MutableState<Boolean>) : Color {
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