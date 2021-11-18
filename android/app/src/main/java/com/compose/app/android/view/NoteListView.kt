package com.compose.app.android.view

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.compose.app.android.R
import com.compose.app.android.components.ExperimentalStaggeredVerticalGrid
import com.compose.app.android.model.NoteColorResourceIDs
import com.compose.app.android.model.NoteColorUniversalIDs
import com.compose.app.android.model.NoteDocument
import com.compose.app.android.viewmodel.ProductivityViewModel
import kotlinx.coroutines.delay

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ExperimentalNoteListView(
    viewModel: ProductivityViewModel,
    onItemLongClick: (NoteDocument) -> Unit,
    onItemClick: (NoteDocument) -> Unit
) {
    val listVisibility = remember { mutableStateOf(false) }
    val listItems = viewModel.noteLiveList.observeAsState().value
    if (listItems!!.isEmpty()) {
        listVisibility.value = false
    }
    if (listItems.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, start = 14.dp, end = 15.dp),
        ) {
            item {
                ExperimentalStaggeredVerticalGrid(
                    maxColumnWidth = 220.dp,
                    content = @Composable {
                        listItems.forEachIndexed { index, document ->
                            NoteListCard(
                                index = index,
                                currentNote = document,
                                onItemClick = onItemClick,
                                onItemLongClick = onItemLongClick,
                                visibilityState = listVisibility
                            )
                        }
                    }
                )
            }
        }
        LaunchedEffect(key1 = true) {
            delay(200L)
            listVisibility.value = true
        }
    } else {
        if (!viewModel.isUpdatingNoteList.isRefreshing) {
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
fun NoteListCard(
    index: Int,
    currentNote: NoteDocument,
    onItemClick: (NoteDocument) -> Unit,
    onItemLongClick: (NoteDocument) -> Unit,
    visibilityState: MutableState<Boolean>
) {
    val cardColor = NoteColorResourceIDs[NoteColorUniversalIDs.indexOf(currentNote.color)]
    AnimatedVisibility(
        visible = visibilityState.value,
        enter = fadeIn(initialAlpha = 0F),
        exit = fadeOut()
    ) {
        Card(
            shape = RoundedCornerShape(7.dp),
            backgroundColor = if (MaterialTheme.colors.isLight) {
                colorResource(id = cardColor).copy(0.7F)
            } else {
                colorResource(id = cardColor)
            },
            elevation = 0.dp,
            modifier = Modifier
                .padding(4.dp)
                .combinedClickable(
                    onClick = {
                        onItemClick.invoke(currentNote)
                    },
                    onLongClick = {
                        onItemLongClick.invoke(currentNote)
                    }
                )
                .animateEnterExit(
                    enter = slideInVertically(
                        animationSpec = spring(dampingRatio = 0.9F),
                        initialOffsetY = {
                            it * (index + 1)
                        }
                    )
                ),
            content = @Composable {
                Column(
                    modifier = Modifier.wrapContentHeight()
                ) {
                    Text(
                        text = currentNote.title,
                        style = MaterialTheme.typography.h6,
                        color = Color.Black,
                        modifier = Modifier.padding(
                            top = 7.dp,
                            end = 10.dp,
                            start = 10.dp
                        )
                    )
                    Text(
                        text = currentNote.content,
                        style = MaterialTheme.typography.body1,
                        color = Color.Black,
                        maxLines = 8,
                        modifier = Modifier.padding(
                            top = 2.dp,
                            end = 10.dp,
                            start = 10.dp
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = currentNote.date,
                            style = MaterialTheme.typography.body1,
                            color = colorResource(id = R.color.text_color_disabled_on_color)
                        )
                        Text(
                            text = currentNote.time,
                            style = MaterialTheme.typography.body1,
                            color = colorResource(id = R.color.text_color_disabled_on_color)
                        )
                    }
                }
            }
        )
    }
}