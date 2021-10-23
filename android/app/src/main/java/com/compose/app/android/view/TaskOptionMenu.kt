package com.compose.app.android.view

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.app.android.R
import com.compose.app.android.components.OptionListItem
import com.compose.app.android.components.SheetHandle
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import com.compose.app.android.presentation.NavigationDestination
import com.compose.app.android.theme.IconEditPen
import com.compose.app.android.theme.IconShareMenu
import com.compose.app.android.theme.IconTrashItem
import com.compose.app.android.viewmodel.ProductivityViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun TaskOptionMenu(
    viewModel: ProductivityViewModel,
    navController: NavController,
    bottomSheetState: ModalBottomSheetState
) {
    val composeAsync = rememberCoroutineScope()
    val taskDocument by viewModel.bottomSheetTaskDocument.observeAsState()
    Column {
        SheetHandle()
        Text(
            text = taskDocument?.taskTitle ?: "Error",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp, start = 20.dp)
        )
        Text(
            text = String.format(
                stringResource(id = R.string.task_option_menu_ends_on_template),
                taskDocument?.dueDate,
                taskDocument?.dueTime
            ),
            style = MaterialTheme.typography.body1,
            color = colorResource(id = R.color.text_color_disabled),
            modifier = Modifier.padding(start = 20.dp, top = 2.dp)
        )
        Column(
            modifier = Modifier.padding(top = 10.dp)
        ) {
            OptionListItem(
                icon = IconEditPen,
                contentDescription = stringResource(id = R.string.edit_icon_content_desc),
                title = stringResource(id = R.string.note_option_menu_list_item_edit),
                onClick = {
                    navController.navigate("""${NavigationDestination.TaskEditorActivity}/${taskDocument?.taskID}""")
                },
            )
            OptionListItem(
                icon = IconShareMenu,
                contentDescription = stringResource(id = R.string.share_menu_content_desc),
                title = stringResource(id = R.string.task_option_menu_list_item_share),
                onClick = {
                    val taskContent = """${taskDocument?.taskTitle}
                        |${taskDocument?.taskContent}""".trimMargin()
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, taskContent)
                        navController.context.startActivity(Intent.createChooser(this, taskContent))
                    }
                    composeAsync.launch {
                        bottomSheetState.hide()
                    }
                }
            )
            OptionListItem(
                icon = IconTrashItem,
                contentDescription = stringResource(id = R.string.delete_icon_content_desc),
                title = stringResource(id = R.string.task_option_menu_list_item_delete),
                onClick = {
                    composeAsync.launch {
                        bottomSheetState.hide()
                    }
                    FirebaseDocument().deleteDocument(
                        documentID = taskDocument!!.taskID,
                        documentType = DocumentType.TASK
                    )
                    viewModel.updateNoteList()
                }
            )
        }
    }
}