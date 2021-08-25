package com.compose.app.android.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.NoteDocument
import com.compose.app.android.model.TaskDocument
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductivityViewModel : ViewModel() {

    val asyncScope = CoroutineScope(Dispatchers.IO + Job())
    val synchronousScope = CoroutineScope(Dispatchers.Main + Job())

    val bottomSheetNoteDocument: MutableLiveData<NoteDocument> = MutableLiveData()

    val noteLiveList: LiveData<MutableList<NoteDocument>> = MutableLiveData(mutableListOf<NoteDocument>())
    val isUpdatingNoteList = SwipeRefreshState(false)

    val taskLiveList: LiveData<MutableList<TaskDocument>> = MutableLiveData(mutableListOf<TaskDocument>())
    val isUpdatingTaskList = SwipeRefreshState(false)

    /**
     * Fetch the latest version of the user's avatar image from
     * Firebase and set the local image to the newest one.
     * @param filesDir - App-specific file directory path (context.filesDir)
     */
    fun updateToNewestAvatar(filesDir: String) {
        asyncScope.launch {
            FirebaseAccount().sendProfileImageToFile(filesDir)
        }
    }

    /**
     * Get a color to set on the bottom paging icons based on their
     * selected state.
     * @param state - The state variable used to store the icon's
     * selected state
     * @return A Color value to be set to the icon.
     */
    @Composable
    fun getIconColor(state: MutableState<Boolean>) : Color {
        return if (state.value) {
            colorResource(id = R.color.text_color_enabled)
        } else {
            colorResource(id = R.color.text_color_disabled)
        }
    }

    fun updateNoteList() {
        FirebaseDocument().getAllNotes(
            noteLiveList as MutableLiveData<MutableList<NoteDocument>>,
            isUpdatingNoteList
        )
    }

    fun updateTaskList() {
        FirebaseDocument().getAllTasks(
            taskLiveList as MutableLiveData<MutableList<TaskDocument>>,
            isUpdatingTaskList
        )
    }

}