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
package com.compose.app.android.firebase

import androidx.lifecycle.MutableLiveData
import com.compose.app.android.model.DocumentType
import com.compose.app.android.model.NoteDocument
import com.compose.app.android.model.TaskDocument
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred

class FirebaseDocument {

    private val firebaseFirestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth

    private val userdataBasePath = firebaseFirestore.collection("USERDATA")
        .document(firebaseAuth.currentUser!!.uid)
    private val userMetadataBase = Firebase.firestore.collection("METADATA").document("USERS")
        .collection(Firebase.auth.currentUser!!.uid)
    private val storageDocumentPath = userMetadataBase.document("QUOTA-MONITOR")
    private val preferenceDocumentPath = userMetadataBase.document("PREFERENCES")

    /**
     * Fetch all notes in the current user's note folder, updating
     * the LiveData list value with all the notes found once fetched,
     * avoiding the need to use a coroutine in the view to call this
     * function.
     * @param liveData - A mutable LiveData value containing a mutable
     * list of NoteDocument model classes, which will be updated as soon as
     * notes are available from Firebase
     * @param isUpdating - SwipeRefreshState used to manage the swipe
     * refresh view to indicate fetching progress
     */
    fun getAllNotes(
        liveData: MutableLiveData<MutableList<NoteDocument>>,
        isUpdating: SwipeRefreshState,
        query: String? = null
    ) {
        isUpdating.isRefreshing = true
        val noteCollectionPath = userdataBasePath.collection("NOTE-DATA")
        noteCollectionPath.get().addOnSuccessListener { result ->
            if (result.isEmpty) {
                isUpdating.isRefreshing = false
            }
            liveData.value = mutableListOf()
            result.documents.forEach { documentSnapshot ->
                val noteData = documentSnapshot.data
                noteData?.let { document ->
                    fun addNoteData() {
                        val liveDataTemp = liveData.value!!
                        val noteModel = NoteDocument(
                            document["ID"] as String,
                            (document["COLOR"] as Long).toInt(),
                            document["CONTENT"] as String,
                            document["TITLE"] as String,
                            document["DATE"] as String,
                            document["TIME"] as String
                        )
                        liveDataTemp.add(noteModel)
                        liveData.value = liveDataTemp
                        isUpdating.isRefreshing = false
                    }
                    if (query != null) {
                        if ((document["TITLE"] as String).contains(query, true) ||
                            (document["CONTENT"] as String).contains(query, true)
                        ) { addNoteData() }
                    } else { addNoteData() }
                }
            }
        }
    }

    /**
     * Fetch a specific note or task by it's ID (file name)
     * @return A map of the document's keys and values
     * @param documentID - the ID of the document being fetched
     * @param documentType - Specify whether the document is a note
     * or a task using the DocumentType enum.
     */
    suspend fun getDocumentByID(documentID: String, documentType: DocumentType): Map<String, Any> {
        val completableToken = CompletableDeferred<Map<String, Any>>()
        val currentDocumentPath = userdataBasePath.collection(
            if (documentType == DocumentType.NOTE) "NOTE-DATA" else "TASK-DATA"
        ).document(documentID)
        currentDocumentPath.get()
            .addOnSuccessListener {
                it.data.let { note ->
                    completableToken.complete(note ?: mapOf())
                }
            }
            .addOnFailureListener {
                completableToken.complete(mapOf())
            }
        return completableToken.await()
    }

    suspend fun getPreferenceMap() : Map<String, Any> {
        val completableToken = CompletableDeferred<Map<String, Any>>()
        preferenceDocumentPath.get()
            .addOnSuccessListener { document ->
                document.data?.let {
                    completableToken.complete(it)
                }
            }.addOnFailureListener {
                completableToken.complete(mapOf())
            }
        return completableToken.await()
    }

    /**
     * Fetch all tasks in the user's current task folder, updating
     * the LiveData value with all the tasks found once they have
     * been fetched.
     * @param liveData - A mutable LiveData value containing a list
     * of TaskDocument model classes, representing each task
     * @param isUpdating - SwipeRefreshState used to manage the
     * state of a swipe refresh view to indicate the progress of
     * task updating
     */
    fun getAllTasks(
        liveData: MutableLiveData<MutableList<TaskDocument>>,
        isUpdating: SwipeRefreshState,
        query: String? = null
    ) {
        isUpdating.isRefreshing = true
        val taskCollectionPath = userdataBasePath.collection("TASK-DATA")
        taskCollectionPath.get().addOnSuccessListener { result ->
            if (result.isEmpty) {
                isUpdating.isRefreshing = false
            }
            liveData.value = mutableListOf()
            result.documents.forEach { document ->
                document.data?.let {
                    val tempTaskList = liveData.value!!
                    fun addTaskData() {
                        tempTaskList.add(
                            TaskDocument(
                                document["ID"] as String,
                                document["TITLE"] as String,
                                document["LOCATION"] as String? ?: "",
                                document["DUE-DATE-HR"] as String,
                                document["DUE-TIME-HR"] as String,
                                document["COMPLETE"] as Boolean,
                                (document["DUE-DATE-TIME-UNIX"] as Long).toDouble()
                            )
                        )
                        liveData.value = tempTaskList
                        isUpdating.isRefreshing = false
                    }
                    if (query != null) {
                        if ((document["TITLE"] as String).contains(query, true) ||
                            (document["LOCATION"] as String).contains(query, true)
                        ) { addTaskData() }
                    } else { addTaskData() }
                }
            }
        }
    }

    /**
     * Update a single key-value pair in a firebase document, leaving
     * all other fields untouched.
     *
     * @param key - The key of the item in the map that you wish to
     * update
     * @param newValue - The new value to be associated with the key
     * @param documentID - The ID of the document to update, if not
     * the preference document
     * @param documentType - The type of document to update, so that
     * the method knows which path to look through
     */
    fun updateSpecificValue(
        key: String,
        newValue: Any,
        documentID: String? = "",
        documentType: DocumentType
    ) {
        var itemPath: DocumentReference?
        userdataBasePath.apply {
            itemPath = when (documentType) {
                DocumentType.NOTE -> collection("NOTE-DATA").document(documentID!!)
                DocumentType.TASK -> collection("TASK-DATA").document(documentID!!)
                DocumentType.PREFERENCE -> preferenceDocumentPath
            }
        }
        itemPath?.let { path ->
            path.get().addOnCompleteListener { document ->
                document.result?.let { it ->
                    if (it.exists()) {
                        val documentTemp = it.data
                        documentTemp.let { map ->
                            map?.let {
                                it.set(key, newValue)
                                path.set(it, SetOptions.merge())
                            }
                        }
                    } else {
                        path.set(hashMapOf(key to newValue), SetOptions.merge())
                    }
                }
            }
        }
    }

    /**
     * Insert a new document to firebase or save an existing note
     * or task.
     * @param documentFields - A map of the document's keys and values
     * @param documentID - The name of the document to be saved as
     * @param type - Specify whether the document is a note or task
     * using the DocumentType enum
     */
    suspend fun saveDocument(
        documentFields: Map<String, Any>,
        documentID: String,
        type: DocumentType
    ) {
        val noteOrTask = if (type == DocumentType.NOTE) "NOTE-DATA" else "TASK-DATA"
        val documentPath = firebaseFirestore.collection("USERDATA")
            .document(firebaseAuth.currentUser!!.uid).collection(noteOrTask).document(documentID)
        val documentSize = FirebaseUtils.calculateDocumentSize(documentPath)
        documentPath.set(documentFields)
        storageDocumentPath.set(
            mapOf("${noteOrTask}-${documentID}" to documentSize),
            SetOptions.merge()
        )
    }

    /**
     * Remove a document entirely from firebase.
     * @param documentID - The name the document was saved under.
     * @param documentType - Specify type of document (note or task)
     * using the DocumentType enum
     */
    fun deleteDocument(
        documentID: String,
        documentType: DocumentType
    ) {
        val noteOrTask = if (documentType == DocumentType.NOTE) "NOTE-DATA" else "TASK-DATA"
        val documentPath = firebaseFirestore.collection("USERDATA")
            .document(firebaseAuth.currentUser!!.uid).collection(noteOrTask).document(documentID)
        storageDocumentPath.get().addOnSuccessListener {
            it.data?.let { it2 ->
                it2.remove("${noteOrTask}-${documentID}")
                storageDocumentPath.set(it2)
                documentPath.delete()
            }
        }
    }

}