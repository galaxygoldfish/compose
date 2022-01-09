/**
 * Copyright (C) 2021  Sebastian Hriscu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **/
package com.compose.app.android.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.firebase.FirebaseQuota
import com.compose.app.android.model.DocumentType
import com.compose.app.android.model.SubTaskDocument
import com.compose.app.android.notification.TaskNotificationManager
import com.compose.app.android.utilities.getCloudPreferences
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

class TaskEditorViewModel : ViewModel() {

    val titleTextFieldValue = mutableStateOf(TextFieldValue(""))
    val locationTextFieldValue = mutableStateOf(TextFieldValue(""))
    val currentDocumentID = MutableLiveData<String?>(null)
    val previousDocumentID = MutableLiveData<String?>(null)
    val taskCompletionState = mutableStateOf(false)
    val interactionMonitor = mutableStateOf(false)
    val subTaskItemList = mutableStateOf(mutableListOf<SubTaskDocument>())

    private val calendar: Calendar = Calendar.getInstance()
    private val dayIndex = calendar[Calendar.DAY_OF_MONTH]
    private val calendarMinute = calendar[Calendar.MINUTE]
    private val editedMinute = if (calendarMinute.toString().length == 1) "0$calendarMinute" else calendarMinute.toString()

    var monthIndex = mutableStateOf(calendar[Calendar.MONTH] + 1)
    val currentMonth = mutableStateOf("")
    val currentYear = mutableStateOf("2021")
    val selectedDayIndex = mutableStateOf(0)
    val selectedHour = mutableStateOf("0")
    val selectedMinute = mutableStateOf("00")
    val selectionAMPM = mutableStateOf(0)

    val showingStorageAlertDialog = mutableStateOf(false)

    private val asynchronousScope = CoroutineScope(Dispatchers.Default + Job())

    /**
     * Update the text fields to the current note's content or if
     * it's a new note, set all fields to blank, date to the current
     * one and the sub task list to be empty.
     */
    fun updateTaskContents(context: Context) {
        asynchronousScope.launch {
            currentDocumentID.value?.let { id ->
                FirebaseDocument().getDocumentByID(id, DocumentType.TASK).let { taskData ->
                    titleTextFieldValue.value = TextFieldValue(taskData["TITLE"] as String? ?: "")
                    locationTextFieldValue.value = TextFieldValue(taskData["LOCATION"] as String? ?: "")
                    taskCompletionState.value = taskData["COMPLETE"] as Boolean? ?: false
                    val dateData = taskData["DUE-DATE-HR"] as String?
                    val timeData = taskData["DUE-TIME-HR"] as String?
                    updateDefaultValues(timeData, dateData, context)
                    updateSubTaskContents(taskData["SUB-TASK-ITEMS"] as List<Map<String, Any>>?)
                }
            }
        }
    }

    /**
     * Initialize the date variables to their saved values, or set
     * them to the current date's values if they are null.
     *
     * @param timeData - The saved task due time, or null
     * @param dateData - The save task due date, or null
     */
    private fun updateDefaultValues(
        timeData: String?,
        dateData: String?,
        context: Context
    ) {
        if (dateData != null) {
            currentMonth.value = dateData.split(", ")[0].split(" ")[0]
            currentYear.value = dateData.split(", ")[1]
            selectedDayIndex.value = (dateData.split(",")[0].split(" ")[1]).toInt()
            monthIndex.value =
                context.resources.getStringArray(R.array.month_list).indexOf(currentMonth.value)
            interactionMonitor.value = true
        } else {
            taskCompletionState.value = false
            monthIndex.value = calendar[Calendar.MONTH]
            currentMonth.value =
                context.resources.getStringArray(R.array.month_list)[monthIndex.value]
            currentYear.value =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Year.now().value.toString() else "2021"
            selectedDayIndex.value = dayIndex
        }
        if (timeData != null) {
            selectedHour.value = timeData.split(":")[0]
            selectedMinute.value = timeData.split(" ")[0].split(":")[1]
            selectionAMPM.value = if (timeData.split(" ")[1] == "AM") 0 else 1
            interactionMonitor.value = true
        } else {
            selectedHour.value =
                (if (calendar[Calendar.HOUR] == 0) 12 else calendar[Calendar.HOUR]).toString()
            (5 * (editedMinute.toInt() / 5)).let {
                selectedMinute.value = if (it == 5 || it == 0) "0$it" else it.toString()
            }
            selectionAMPM.value = calendar[Calendar.AM_PM]
        }
    }

    /**
     * Refresh the list of sub-tasks to the most recent version
     * available.
     *
     * @param subTaskList - The list of sub-tasks from Firebase
     */
    private fun updateSubTaskContents(subTaskList: List<Map<String, Any>>?) {
        subTaskItemList.value = mutableListOf()
        subTaskList?.forEach { map ->
            subTaskItemList.value.add(
                SubTaskDocument(
                    map["SUB-TASK-NAME"] as String,
                    map["SUB-TASK-COMPLETE"] as Boolean
                )
            )
        }
    }

    /**
     * Parse the list of sub-tasks from a list of SubTaskDocuments
     * to a list of maps containing their respective values, suitable
     * for Firebase.
     *
     * @return - The parsed sub-task list
     */
    private fun parseSubTaskList(): List<Map<String, Any>> {
        val returnValue = mutableListOf<Map<String, Any>>()
        subTaskItemList.value.forEach { document ->
            returnValue.add(
                hashMapOf(
                    "SUB-TASK-NAME" to (document.taskName ?: ""),
                    "SUB-TASK-COMPLETE" to document.taskComplete
                )
            )
        }
        return returnValue
    }

    /**
     * Save and upload the current task's data to Firebase,
     * and if a time was selected, schedule a notification for
     * that time.
     */
    suspend fun saveTaskData(context: Context) : Boolean {
        val completableDeferred = CompletableDeferred<Boolean>()
        if (titleTextFieldValue.value.text.isNotEmpty()) {
            if (FirebaseQuota.calculateUserStorage() < 5000000) {
                asynchronousScope.launch {
                    val dueTime =
                        "${selectedHour.value}:${selectedMinute.value} ${if (selectionAMPM.value == 0) "AM" else "PM"}"
                    val baseTimeFormat = SimpleDateFormat("MMMM d h:mm a yyyy", Locale.ENGLISH)
                    val parsedDueDate = baseTimeFormat.parse(
                        "${currentMonth.value} ${selectedDayIndex.value} $dueTime ${currentYear.value}"
                    )
                    val taskDataMap = mapOf(
                        "ID" to (currentDocumentID.value ?: UUID.randomUUID().toString()),
                        "TITLE" to titleTextFieldValue.value.text,
                        "LOCATION" to locationTextFieldValue.value.text,
                        "COMPLETE" to taskCompletionState.value,
                        "DUE-TIME-HR" to dueTime,
                        "DUE-DATE-HR" to "${currentMonth.value} ${selectedDayIndex.value}, ${currentYear.value}",
                        "DUE-DATE-TIME-UNIX" to (parsedDueDate?.time ?: 0),
                        "SUB-TASK-ITEMS" to parseSubTaskList()
                    )
                    Log.e("COMPOSE", "TaskEditorViewModel#saveTaskData")
                    if (
                        parsedDueDate!!.time > Date().time &&
                        context.getCloudPreferences()
                            .getBoolean("STATE_ENABLE_NOTIFICATIONS", false)
                    ) {
                        TaskNotificationManager.scheduleTaskNotification(
                            context = context,
                            taskID = currentDocumentID.value!!,
                            content = String.format(
                                context.getString(R.string.notification_content_format),
                                titleTextFieldValue.value.text
                            ),
                            title = context.getString(R.string.notification_title_format),
                            timeUnix = parsedDueDate.time
                        )
                    }
                    FirebaseDocument().saveDocument(
                        documentID = currentDocumentID.value ?: UUID.randomUUID().toString(),
                        documentFields = taskDataMap,
                        type = DocumentType.TASK
                    )
                    completableDeferred.complete(true)
                }
            } else {
                showingStorageAlertDialog.value = true
                completableDeferred.complete(false)
            }
        }
        return completableDeferred.await()
    }

}