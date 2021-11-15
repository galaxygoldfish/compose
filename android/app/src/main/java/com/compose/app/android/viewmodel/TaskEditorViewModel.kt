package com.compose.app.android.viewmodel

import android.content.Context
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.R
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import com.compose.app.android.notification.TaskNotificationManager
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
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
    private val editedMinute =
        if (calendarMinute.toString().length == 1) "0$calendarMinute" else calendarMinute.toString()

    var monthIndex = mutableStateOf(calendar[Calendar.MONTH])
    val currentMonth = mutableStateOf("")
    val currentYear = mutableStateOf("2021")
    val selectedDayIndex = mutableStateOf(0)
    val selectedHour = mutableStateOf("0")
    val selectedMinute = mutableStateOf("00")
    val selectionAMPM = mutableStateOf(0)

    private val asynchronousScope = CoroutineScope(Dispatchers.IO + Job())

    fun updateTaskContents(context: Context) {
        asynchronousScope.launch {
            currentDocumentID.value?.let { id ->
                FirebaseDocument().getDocumentByID(id, DocumentType.TASK).let { taskData ->
                    titleTextFieldValue.value = TextFieldValue(taskData["TITLE"] as String? ?: "")
                    contentTextFieldValue.value =
                        TextFieldValue(taskData["CONTENT"] as String? ?: "")
                    taskCompletionState.value = taskData["COMPLETE"] as Boolean? ?: false
                    val dateData = taskData["DUE-DATE-HR"] as String?
                    val timeData = taskData["DUE-TIME-HR"] as String?
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
            monthIndex.value =
                context.resources.getStringArray(R.array.month_list).indexOf(currentMonth.value)
            interactionMonitor.value = true
        } else {
            taskCompletionState.value = false
            monthIndex.value = calendar[Calendar.MONTH]
            currentMonth.value = context.resources.getStringArray(R.array.month_list)[monthIndex.value]
            currentYear.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Year.now().value.toString() else "2021"
            selectedDayIndex.value = dayIndex
        }
        if (timeData != null) {
            selectedHour.value = timeData.split(":")[0]
            selectedMinute.value = timeData.split(" ")[0].split(":")[1]
            selectionAMPM.value = if (timeData.split(" ")[1] == "AM") 0 else 1
            interactionMonitor.value = true
        } else {
            selectedHour.value = (if (calendar[Calendar.HOUR] == 0) 12 else calendar[Calendar.HOUR]).toString()
            (5 * (editedMinute.toInt() / 5)).let {
                selectedMinute.value = if (it == 5 || it == 0) "0$it" else it.toString()
            }
            selectionAMPM.value = calendar[Calendar.AM_PM]
        }
    }

    fun saveTaskData(context: Context) {
        if (titleTextFieldValue.value.text.isNotEmpty()) {
            asynchronousScope.launch {
                val dueTime =
                    "${selectedHour.value}:${selectedMinute.value} ${if (selectionAMPM.value == 0) "AM" else "PM"}"
                val baseTimeFormat = SimpleDateFormat("MMMM d h:mm a yyyy", Locale.ENGLISH)
                val parsedDueDate =
                    baseTimeFormat.parse("${currentMonth.value} ${selectedDayIndex.value} $dueTime ${currentYear.value}")
                val taskDataMap = mapOf(
                    "ID" to (currentDocumentID.value ?: UUID.randomUUID().toString()),
                    "TITLE" to titleTextFieldValue.value.text,
                    "CONTENT" to contentTextFieldValue.value.text,
                    "COMPLETE" to taskCompletionState.value,
                    "DUE-TIME-HR" to dueTime,
                    "DUE-DATE-HR" to "${currentMonth.value} ${selectedDayIndex.value}, ${currentYear.value}",
                    "DUE-DATE-TIME-UNIX" to (parsedDueDate?.time ?: 0)
                )
                if (Date().time >= parsedDueDate?.time ?: 0) {
                    TaskNotificationManager.scheduleTaskNotification(
                        context = context,
                        taskID = currentDocumentID.value!!,
                        content = String.format(
                            context.getString(R.string.notification_content_format),
                            titleTextFieldValue.value.text
                        ),
                        title = context.getString(R.string.notification_title_format),
                        timeUnix = parsedDueDate?.time ?: 0
                    )
                }
                FirebaseDocument().saveDocument(
                    documentID = currentDocumentID.value ?: UUID.randomUUID().toString(),
                    documentFields = taskDataMap,
                    type = DocumentType.TASK
                )
            }
        }
    }

}