package com.compose.app.android.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.firebase.FirebaseAccount
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.firebase.FirebaseUtils
import com.compose.app.android.model.ExpandableFABState
import com.compose.app.android.model.NoteDocument
import com.compose.app.android.model.TaskDocument
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductivityViewModel : ViewModel() {

    private val asyncScope = CoroutineScope(Dispatchers.IO + Job())

    val bottomSheetNoteDocument: MutableLiveData<NoteDocument> = MutableLiveData()
    val bottomSheetTaskDocument: MutableLiveData<TaskDocument> = MutableLiveData()

    val noteLiveList: LiveData<MutableList<NoteDocument>> = MutableLiveData(mutableListOf())
    val isUpdatingNoteList = SwipeRefreshState(false)

    val taskLiveList: LiveData<MutableList<TaskDocument>> = MutableLiveData(mutableListOf())
    val isUpdatingTaskList = SwipeRefreshState(false)

    val avatarImageStore: MutableState<Bitmap?> = mutableStateOf(null)
    val showProfileContextDialog = mutableStateOf(false)

    val floatingActionState = mutableStateOf(ExpandableFABState.COLLAPSED)
    val taskSelectedState = mutableStateOf(false)
    val noteSelectedState = mutableStateOf(true)
    val searchFieldValue = mutableStateOf(TextFieldValue())

    val userStorageSize = mutableStateOf(0)

    /**
     * Fetch the latest version of the user's avatar image from
     * Firebase and set the local image to the newest one.
     * @param filesDir - App-specific file directory path (context.filesDir)
     */
    fun updateToNewestAvatar(filesDir: String) {
        asyncScope.launch {
            FirebaseAccount().sendProfileImageToFile(filesDir)
            avatarImageStore.value = BitmapFactory.decodeFile("${filesDir}/avatar.png")
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
            MaterialTheme.colors.onBackground
        } else {
            MaterialTheme.colors.onBackground.copy(0.5F)
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

    fun updateStorageCount() {
        asyncScope.launch {
            userStorageSize.value = FirebaseUtils.calculateUserStorage()
        }
    }

}