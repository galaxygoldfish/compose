package com.compose.app.android.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Year
import java.util.*

class TaskEditorViewModel : ViewModel() {

    val titleTextFieldValue = mutableStateOf(TextFieldValue(""))
    val contentTextFieldValue = mutableStateOf(TextFieldValue(""))
    val currentDocumentID = MutableLiveData<String?>(null)
    val previousDocumentID = MutableLiveData<String?>(null)
    val taskCompletionState = mutableStateOf(false)
    val interactionMonitor = mutableStateOf(false)

    private val calendar: Calendar = Calendar.getInstance()
    private val dayIndex = calendar[Calendar.DAY_OF_MONTH]
    private val calendarMinute = calendar[Calendar.MINUTE]
    private val editedMinute = if (calendarMinute.toString().length == 1) "0$calendarMinute" else calendarMinute.toString()

    var monthIndex = mutableStateOf(calendar[Calendar.MONTH])
    val currentMonth = mutableStateOf("")
    val currentYear = mutableStateOf("2021")
    val selectedDayIndex = mutableStateOf(0)
    val selectedHour = mutableStateOf("")
    val selectedMinute = mutableStateOf("")
    val selectionAMPM = mutableStateOf(0)

    private val asynchronousScope = CoroutineScope(Dispatchers.IO + Job())
    private val synchronousScope = CoroutineScope(Dispatchers.Main + Job())

    fun updateTaskContents(context: Context) {
        asynchronousScope.launch {
            currentDocumentID.value?.let { id ->
                FirebaseDocument().getDocumentByID(id, DocumentType.TASK).let { taskData ->
                    titleTextFieldValue.value = TextFieldValue(taskData["title"] as String? ?: "")
                    contentTextFieldValue.value = TextFieldValue(taskData["content"] as String? ?: "")
                    taskCompletionState.value = taskData["complete"] as Boolean? ?: false
                    val dateData = taskData["dueDateHumanReadable"] as String?
                    val timeData = taskData["dueTimeHumanReadable"] as String?
                    updateDateValues(timeData, dateData, context)
                }
            }
        }
    }

    private fun updateDateValues(
        timeData: String?,
        dateData: String?,
        context: Context
    ) {
        if (dateData != null) {
            currentMonth.value = dateData.split(", ")[0].split(" ")[0]
            currentYear.value = dateData.split(", ")[1]
            selectedDayIndex.value = (dateData.split(",")[0].split(" ")[1]).toInt()
            monthIndex.value = context.resources.getStringArray(R.array.month_list).indexOf(currentMonth.value)
            interactionMonitor.value = true
        } else {
            taskCompletionState.value = false
            monthIndex.value = calendar[Calendar.MONTH]
            currentMonth.value = context.resources.getStringArray(R.array.month_list)[monthIndex.value]
            currentYear.value = Year.now().value.toString()
            selectedDayIndex.value = dayIndex
        }
        if (timeData != null) {
            selectedHour.value = timeData.split(":")[0]
            selectedMinute.value = timeData.split(" ")[0].split(":")[1]
            selectionAMPM.value = if (timeData.split(" ")[1] == "AM") 0 else 1
            interactionMonitor.value = true
        } else {
            selectedHour.value = (if (calendar[Calendar.HOUR] == 0) 12 else calendar[Calendar.HOUR]).toString()
            selectedMinute.value = editedMinute
            selectionAMPM.value = calendar[Calendar.AM_PM]
        }
    }

    /// TODO - Calculate unix time for combined date string and save to firebase
    fun saveTaskData() {
        if (titleTextFieldValue.value.text.isNotEmpty()) {
            val taskDataMap = mapOf(
                "ID" to (currentDocumentID.value ?: UUID.randomUUID().toString()),
                "title" to titleTextFieldValue.value.text,
                "content" to contentTextFieldValue.value.text,
                "complete" to taskCompletionState.value,
                "dueTimeHumanReadable" to "${selectedHour.value}:${selectedMinute.value} ${
                    if (selectionAMPM.value == 0) "AM" else "PM"
                }",
                "dueDateHumanReadable" to "${currentMonth.value} ${selectedDayIndex.value}, ${currentYear.value}",
                "dueDateTimeUnix" to 11111111111111 // temporary value
            )
            asynchronousScope.launch {
                FirebaseDocument().saveDocument(
                    documentID = currentDocumentID.value ?: UUID.randomUUID().toString(),
                    documentFields = taskDataMap,
                    type = DocumentType.TASK
                )
            }
        }
    }

}