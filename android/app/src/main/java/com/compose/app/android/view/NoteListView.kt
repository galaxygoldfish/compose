package com.compose.app.android.view

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.compose.app.android.R
import com.compose.app.android.components.ExperimentalStaggeredVerticalGrid
import com.compose.app.android.model.NoteColorResourceIDs
import com.compose.app.android.model.NoteColorUniversalIDs
import com.compose.app.android.model.NoteDocument

@Composable
@ExperimentalFoundationApi
@ExperimentalMaterialApi
fun NoteListView(
    noteItemList: MutableLiveData<MutableList<NoteDocument>>,
    context: Context,
    onItemClick: (NoteDocument) -> Unit,
    onItemLongClick: (NoteDocument) -> Unit
) {
    val gridCellValue = remember { mutableStateOf(GridCells.Fixed(2)) }
    val listItems = noteItemList.observeAsState().value!!
    LazyVerticalGrid(
        cells = gridCellValue.value,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp, start = 14.dp, end = 15.dp),
        content = {
            items(
                count = listItems.size,
                itemContent = @Composable { index ->
                    val currentNote = listItems[index]
                    NoteListCard(
                        index = index,
                        currentNote = currentNote,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick
                    )
                }
            )
        }
    )
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ExperimentalNoteListView(
    noteItemList: MutableLiveData<MutableList<NoteDocument>>,
    context: Context,
    onItemLongClick: (NoteDocument) -> Unit,
    onItemClick: (NoteDocument) -> Unit
) {
    val listItems = noteItemList.observeAsState().value
    if (listItems != null && listItems.isNotEmpty()) {
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
                                onItemLongClick = onItemLongClick
                            )
                        }
                    }
                )
            }
        }
    } else {
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

@ExperimentalFoundationApi
@Composable
@ExperimentalMaterialApi
fun NoteListCard(
    index: Int,
    currentNote: NoteDocument,
    onItemClick: (NoteDocument) -> Unit,
    onItemLongClick: (NoteDocument) -> Unit
) {
    Card(
        shape = RoundedCornerShape(7.dp),
        backgroundColor = colorResource(id = NoteColorResourceIDs[
                NoteColorUniversalIDs.indexOf(currentNote.color)
        ]),
        elevation = 0.dp,
        modifier = Modifier
            .padding(
                4.dp
            )
            .combinedClickable(
                onClick = {
                    onItemClick.invoke(currentNote)
                },
                onLongClick = {
                    onItemLongClick.invoke(currentNote)
                }
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