package com.compose.app.android.view

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.GridCells
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.compose.app.android.R
import com.compose.app.android.model.NoteDocument

@Composable
@ExperimentalFoundationApi
@ExperimentalMaterialApi
fun NoteListView(
    noteItemList: MutableLiveData<MutableList<NoteDocument>>,
    context: Context,
    onItemClick: () -> Unit
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
                        onItemClick = onItemClick
                    )
                }
            )
        }
    )
}

@Composable
@ExperimentalMaterialApi
fun NoteListCard(
    index: Int,
    currentNote: NoteDocument,
    onItemClick: () -> Unit
) {
    val isCardEven = index % 2 == 0
    val paddingStart = if (!isCardEven) 3.dp else 0.dp
    val paddingEnd = if (!isCardEven) 0.dp else 6.dp
    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(7.dp),
        backgroundColor = colorResource(id = currentNote.color),
        elevation = 0.dp,
        modifier = Modifier.padding(
            top = 5.dp,
            bottom = 5.dp,
            start = paddingStart,
            end = paddingEnd
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
                        style = MaterialTheme.typography.body2,
                        color = colorResource(id = R.color.text_color_disabled_on_color)
                    )
                    Text(
                        text = currentNote.time,
                        style = MaterialTheme.typography.body2,
                        color = colorResource(id = R.color.text_color_disabled_on_color)
                    )
                }
            }
        }
    )
}