package com.compose.app.android.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.compose.app.android.R
import com.compose.app.android.model.NoteColorResourceIDs
import com.compose.app.android.model.NoteColorUniversalIDs
import com.compose.app.android.theme.IconCheckMark
import com.compose.app.android.theme.IconThemeColor

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun NoteColorPickerSheet(
    currentNoteColor: MutableLiveData<Int>,
    currentColorCentral: MutableLiveData<Int>
) {
    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 15.dp)
        ) {
            Icon(
                painter = painterResource(id = IconThemeColor),
                contentDescription = stringResource(id = R.string.palette_icon_content_desc),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(30.dp)
            )
            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.note_editor_color_picker_title),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.note_editor_color_picker_subtitle),
                    style = MaterialTheme.typography.body1,
                )
            }
        }
        LazyVerticalGrid(
            cells = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 15.dp, end = 15.dp, bottom = 10.dp),
            content = {
                items(NoteColorResourceIDs.size) { index ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = colorResource(id = NoteColorResourceIDs[index]),
                        elevation = 0.dp,
                        modifier = Modifier
                            .width(1000.dp)
                            .height(90.dp)
                            .padding(5.dp),
                        onClick = {
                            currentNoteColor.value = NoteColorResourceIDs[index]
                            currentColorCentral.value = NoteColorUniversalIDs[index]
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val stateNoteColor by currentNoteColor.observeAsState()
                            if (stateNoteColor == NoteColorResourceIDs[index]) {
                                Icon(
                                    painter = painterResource(id = IconCheckMark),
                                    contentDescription = stringResource(id = R.string.check_mark_content_desc),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}