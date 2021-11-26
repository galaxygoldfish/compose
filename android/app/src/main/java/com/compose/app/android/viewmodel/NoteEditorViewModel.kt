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

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.*
import com.compose.app.android.model.SavedSpanType.BOLD_SPAN
import com.compose.app.android.model.SavedSpanType.COLOR_BLUE
import com.compose.app.android.model.SavedSpanType.COLOR_GREEN
import com.compose.app.android.model.SavedSpanType.COLOR_ORANGE
import com.compose.app.android.model.SavedSpanType.COLOR_PURPLE
import com.compose.app.android.model.SavedSpanType.COLOR_RED
import com.compose.app.android.model.SavedSpanType.COLOR_SPAN
import com.compose.app.android.model.SavedSpanType.COLOR_YELLOW
import com.compose.app.android.model.SavedSpanType.ITALIC_SPAN
import com.compose.app.android.model.SavedSpanType.SIZE_SPAN
import com.compose.app.android.model.SavedSpanType.UNDERLINE_SPAN
import com.compose.app.android.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.max
import kotlin.math.min

class NoteEditorViewModel : ViewModel() {

    val noteDocumentID: MutableLiveData<String?> = MutableLiveData(null)
    val previousDocumentID: MutableLiveData<String> = MutableLiveData("")
    val selectedNoteColorRes: MutableLiveData<Int> = MutableLiveData(NoteColorResourceIDs[7])
    val selectedNoteColorCentral: MutableLiveData<Int> = MutableLiveData(NoteColorUniversalIDs[7])

    var titleTextValue = mutableStateOf(TextFieldValue())
    var contentTextValue = mutableStateOf(TextFieldValue())

    val noteFormatList = mutableStateOf(mutableListOf<TextFormatSpan>())
    var styleSaveList = mutableListOf<MutableMap<String, Int>>()
    val showFormatOptionDialog = mutableStateOf(false)
    val currentDialogResource = mutableStateOf(0) // 0 for size, 1 for color

    private val asynchronousScope = CoroutineScope(Dispatchers.IO + Job())
    private val synchronousScope = CoroutineScope(Dispatchers.Main + Job())

    /**
     * Fetch the current note's contents from the cloud or clear
     * all text fields and customization values to leave the editor
     * blank.
     */
    fun updateNoteContents() {
        showFormatOptionDialog.value = false
        asynchronousScope.launch {
            if (previousDocumentID.value != noteDocumentID.value) {
                noteDocumentID.value?.let { id ->
                    FirebaseDocument().getDocumentByID(id, DocumentType.NOTE).let { noteData ->
                        titleTextValue.value = TextFieldValue(noteData["TITLE"] as String? ?: "")
                        contentTextValue.value = TextFieldValue(noteData["CONTENT"] as String? ?: "")
                        (noteData["STYLE"] as List<Map<String, Int>>?)?.let { processAnnotatedString(it) }
                        synchronousScope.launch {
                            selectedNoteColorCentral.value = (noteData["COLOR"] as Long? ?: CardColorBlueAlt).toInt()
                            selectedNoteColorRes.value = NoteColorResourceIDs[
                                    NoteColorUniversalIDs.indexOf((noteData["COLOR"] as Long? ?: CardColorBlueAlt).toInt())
                            ]
                        }
                    }
                }
            }
        }
    }

    /**
     * Upload note contents to firebase only if there is note content
     * to save.
     */
    fun saveNoteContents() {
        if (titleTextValue.value.text.isNotEmpty() || contentTextValue.value.text.isNotEmpty()) {
            val noteDocumentMap = mapOf(
                "ID" to noteDocumentID.value!!,
                "TITLE" to titleTextValue.value.text,
                "CONTENT" to contentTextValue.value.text,
                "COLOR" to selectedNoteColorCentral.value!!,
                "DATE" to getCurrentDate(),
                "TIME" to getCurrentTime(),
                "STYLE" to styleSaveList
            )
            asynchronousScope.launch {
                FirebaseDocument().saveDocument(
                    documentFields = noteDocumentMap,
                    documentID = noteDocumentID.value!!,
                    type = DocumentType.NOTE
                )
            }
        }
    }

    /**
     * Add a new span to the saved formatting list
     */
    fun addSpan(spanStyle: SpanStyle, type: Int, extra: Int? = null) {
        if (type != SavedSpanType.NULL) {
            val start = contentTextValue.value.selection.start
            val end = contentTextValue.value.selection.end
            noteFormatList.value.add(
                TextFormatSpan(
                    spanType = spanStyle,
                    spanStart = min(start, end),
                    spanEnd = max(start, end)
                )
            )
            val savedMap = mutableMapOf(
                "SPAN-TYPE" to type,
                "SPAN-START" to min(start, end),
                "SPAN-END" to max(start, end)
            )
            if (extra != null) { savedMap["SPAN-EXTRA"] = extra }
            styleSaveList.add(savedMap)
            reformatNote()
        }
    }

    /**
     * Set all editor text fields to be blank
     */
    fun clearTextFields() {
        contentTextValue = mutableStateOf(TextFieldValue(""))
        titleTextValue = mutableStateOf(TextFieldValue(""))
        styleSaveList = mutableListOf()
        noteFormatList.value = mutableListOf()
        styleSaveList.clear()
    }

    /**
     * Iterate through text style list and refresh the
     * noteFormatList with all of the updated spans to
     * be applied to the text
     */
    private fun processAnnotatedString(savedSpans: List<Map<String, Int>>?) {
        noteFormatList.value.clear()
        savedSpans?.let { list ->
            fun getSpanType(map: Map<String, Any>) : SpanStyle {
                return when ((map["SPAN-TYPE"] as Long).toInt()) {
                    BOLD_SPAN -> SpanStyle(fontWeight = FontWeight.SemiBold)
                    ITALIC_SPAN -> SpanStyle(fontStyle = FontStyle.Italic)
                    UNDERLINE_SPAN -> SpanStyle(textDecoration = TextDecoration.Underline)
                    SIZE_SPAN -> SpanStyle(fontSize = (map["SPAN-EXTRA"]!! as Long).toFloat().sp)
                    COLOR_SPAN -> {
                        SpanStyle(
                            color = when ((map["SPAN-EXTRA"] as Long).toInt()) {
                                COLOR_RED -> TextColorRed
                                COLOR_ORANGE -> TextColorOrange
                                COLOR_YELLOW -> TextColorYellow
                                COLOR_GREEN -> TextColorGreen
                                COLOR_BLUE -> TextColorBlue
                                COLOR_PURPLE -> TextColorPurple
                                else -> if (currentAppThemeState.value) Color.White else Color.Black
                            }
                        )
                    }
                    else -> SpanStyle()
                }
            }
            list.forEach { map ->
                val start = (map["SPAN-END"] as Long).toInt()
                val end = (map["SPAN-START"] as Long).toInt()
                val savedMap = mutableMapOf(
                    "SPAN-TYPE" to (map["SPAN-TYPE"] as Long).toInt(),
                    "SPAN-START" to start,
                    "SPAN-END" to end,
                    "SPAN-EXTRA" to ((map["SPAN-EXTRA"] as Long?)?.toInt() ?: 0)
                )
                styleSaveList.add(savedMap)
                noteFormatList.value.add(
                    TextFormatSpan(
                        spanStart = start,
                        spanEnd = end,
                        spanType = getSpanType(map)
                    )
                )
            }
            reformatNote()
        }
    }

    /**
     * Apply formatting effects to note text being rendered,
     * reading from the noteFormatList from processAnnotatedString
     */
    private fun reformatNote() {
        val text = contentTextValue.value
        val listChanges: MutableList<AnnotatedString.Range<SpanStyle>> = mutableListOf()
        noteFormatList.value.forEach { span ->
            val start = span.spanStart
            val end = span.spanEnd
            listChanges.add(AnnotatedString.Range(span.spanType, min(start, end), max(start, end)))
        }
        contentTextValue.value = TextFieldValue(
            AnnotatedString(text.text, listChanges, text.annotatedString.paragraphStyles)
        )
    }

    /**
     * Return a formatted string of the current date
     */
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return """${calendar[Calendar.MONTH] + 1}/${calendar[Calendar.DATE]}"""
    }

    /**
     * Return a parsed string of the current time
     */
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val calendarMinute = calendar[Calendar.MINUTE]
        val editedMinute =
            if (calendarMinute.toString().length == 1) "0$calendarMinute" else calendarMinute
        return """${calendar[Calendar.HOUR]}:${editedMinute}"""
    }
}