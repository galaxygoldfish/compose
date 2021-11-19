package com.compose.app.android.view

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.components.OptionListItem
import com.compose.app.android.components.SheetHandle
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import com.compose.app.android.model.NoteColorResourceIDs
import com.compose.app.android.model.NoteColorUniversalIDs
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.IconEditPen
import com.compose.app.android.theme.IconShareMenu
import com.compose.app.android.theme.IconTrashItem
import com.compose.app.android.viewmodel.ProductivityViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch


@ExperimentalAnimationApi
@Composable
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
fun NoteOptionMenu(
    viewModel: ProductivityViewModel,
    navController: NavController,
    bottomSheetState: ModalBottomSheetState
) {
    val noteDocument by viewModel.bottomSheetNoteDocument.observeAsState()
    val composeAsync = rememberCoroutineScope()
    Column {
        SheetHandle()
        Text(
            text = noteDocument?.title ?: "Error",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp, start = 20.dp)
        )
        Text(
            text = String.format(
                stringResource(id = com.compose.app.android.R.string.note_option_menu_last_edited),
                noteDocument?.date,
                noteDocument?.time
            ),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(start = 20.dp, top = 5.dp)
        )
        LazyRow(
            modifier = Modifier.padding(start = 20.dp, top = 15.dp, end = 10.dp),
            content = {
                val selectedIndex =
                    mutableStateOf(NoteColorUniversalIDs.indexOf(noteDocument?.color))
                items(NoteColorResourceIDs.size) { index ->
                    val selectedItem = mutableStateOf(index == selectedIndex.value)
                    Card(
                        shape = CircleShape,
                        elevation = 0.dp,
                        backgroundColor = colorResource(id = NoteColorResourceIDs[index]),
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(30.dp),
                        onClick = {
                            noteDocument?.apply {
                                val selection = NoteColorUniversalIDs[index]
                                selectedIndex.value = NoteColorUniversalIDs.indexOf(selection)
                                composeAsync.launch {
                                    FirebaseDocument().saveDocument(
                                        documentFields = mapOf(
                                            "TITLE" to title,
                                            "CONTENT" to content,
                                            "DATE" to date,
                                            "TIME" to time,
                                            "COLOR" to selection,
                                            "ID" to noteID
                                        ),
                                        documentID = noteID,
                                        type = DocumentType.NOTE
                                    )
                                }
                            }
                            viewModel.updateNoteList()
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (selectedItem.value) {
                                Card(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.Center),
                                    backgroundColor = Color(0xFF202020),
                                    shape = CircleShape,
                                    elevation = 0.dp
                                ) {
                                }
                            }
                        }
                    }
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
        ) {
            OptionListItem(
                icon = IconEditPen,
                contentDescription = stringResource(id = com.compose.app.android.R.string.edit_icon_content_desc),
                title = stringResource(id = com.compose.app.android.R.string.note_option_menu_list_item_edit),
                onClick = {
                    navController.navigate("""${NavigationDestination.NoteEditorActivity}/${noteDocument?.noteID}""")
                }
            )
            OptionListItem(
                icon = IconShareMenu,
                contentDescription = stringResource(id = com.compose.app.android.R.string.share_menu_content_desc),
                title = stringResource(id = com.compose.app.android.R.string.note_option_menu_list_item_share),
                onClick = {
                    val noteContent = """${noteDocument?.title}
                        |${noteDocument?.content}""".trimMargin()
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, noteContent)
                        navController.context.startActivity(Intent.createChooser(this, noteContent))
                    }
                    composeAsync.launch {
                        bottomSheetState.hide()
                    }
                }
            )
            OptionListItem(
                icon = IconTrashItem,
                contentDescription = stringResource(id = com.compose.app.android.R.string.delete_icon_content_desc),
                title = stringResource(id = com.compose.app.android.R.string.note_option_menu_list_item_delete),
                onClick = {
                    composeAsync.launch {
                        bottomSheetState.hide()
                    }
                    FirebaseDocument().deleteDocument(
                        documentID = noteDocument!!.noteID,
                        documentType = DocumentType.NOTE
                    )
                    viewModel.updateNoteList()
                }
            )
        }
    }
}