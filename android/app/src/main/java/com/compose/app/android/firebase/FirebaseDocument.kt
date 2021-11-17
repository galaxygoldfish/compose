package com.compose.app.android.firebase

import androidx.lifecycle.MutableLiveData
import com.compose.app.android.model.DocumentType
import com.compose.app.android.model.NoteDocument
import com.compose.app.android.model.TaskDocument
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred

class FirebaseDocument {

    private val firebaseFirestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth

    private val userdataBasePath = firebaseFirestore.collection("USERDATA")
        .document(firebaseAuth.currentUser!!.uid)
    private val storageDocumentPath = firebaseFirestore.collection("METADATA").document("USERS")
        .collection(firebaseAuth.currentUser!!.uid).document("QUOTA-MONITOR")

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
        isUpdating: SwipeRefreshState
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
                    val liveDataTemp = liveData.value!!
                    val noteModel = NoteDocument(document["ID"] as String, (document["COLOR"] as Long).toInt(),
                        document["CONTENT"] as String, document["TITLE"] as String, document["DATE"] as String,
                        document["TIME"] as String)
                    liveDataTemp.add(noteModel)
                    liveData.value = liveDataTemp
                    isUpdating.isRefreshing = false
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
    suspend fun getDocumentByID(documentID: String, documentType: DocumentType) : Map<String, Any> {
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
        isUpdating: SwipeRefreshState
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
                    tempTaskList.add(
                        TaskDocument(
                            document["ID"] as String, document["TITLE"] as String,
                            document["LOCATION"] as String, document["DUE-DATE-HR"] as String,
                            document["DUE-TIME-HR"] as String, document["COMPLETE"] as Boolean,
                            (document["DUE-DATE-TIME-UNIX"] as Long).toDouble()
                        )
                    )
                    liveData.value = tempTaskList
                    isUpdating.isRefreshing = false
                }
            }
        }
    }

    /**
     * Update the state of a task's 'isComplete' value, which
     * indicates whether the task is done or not.
     * @param newValue - The new value of the task's complete
     * state to send to Firebase
     * @param taskID - The document ID of the task to be updated.
     */
    fun updateTaskCompletion(
        newValue: Boolean,
        taskID: String
    ) {
        val taskItemPath = userdataBasePath.collection("TASK-DATA").document(taskID)
        taskItemPath.get().addOnSuccessListener { document ->
            val documentTemp = document.data!!
            documentTemp.let {
                it["COMPLETE"] = newValue
            }
            taskItemPath.set(documentTemp, SetOptions.merge())
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
        storageDocumentPath.set(mapOf("${noteOrTask}-${documentID}" to documentSize), SetOptions.merge())
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