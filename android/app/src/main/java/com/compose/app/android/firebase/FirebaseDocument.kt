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

    private val userdataBasePath = firebaseFirestore.collection("userdata")
        .document(firebaseAuth.currentUser!!.uid)

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
        val noteCollectionPath = userdataBasePath.collection("note-data")
        noteCollectionPath.get().addOnSuccessListener { result ->
            if (result.isEmpty) {
                isUpdating.isRefreshing = false
            }
            liveData.value = mutableListOf()
            result.documents.forEach { documentSnapshot ->
                val noteData = documentSnapshot.data
                noteData?.let { document ->
                    val liveDataTemp = liveData.value!!
                    val noteModel = NoteDocument(document["ID"] as String, (document["color"] as Long).toInt(),
                        document["content"] as String, document["title"] as String, document["date"] as String,
                        document["time"] as String)
                    liveDataTemp.add(noteModel)
                    liveData.value = liveDataTemp
                    isUpdating.isRefreshing = false
                }
            }
        }
    }

    // WIP
    suspend fun getNoteByID(documentID: String) : Map<String, Any> {
        val completableToken = CompletableDeferred<Map<String, Any>>()
        val currentNotePath = userdataBasePath.collection("note-data").document(documentID)
        currentNotePath.get().addOnSuccessListener {
            it.data?.let { note ->
                completableToken.complete(note)
            }
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
        val taskCollectionPath = userdataBasePath.collection("task-data")
        taskCollectionPath.get().addOnSuccessListener { result ->
            liveData.value = mutableListOf()
            result.documents.forEach { document ->
                document.data?.let {
                    val tempTaskList = liveData.value!!
                    tempTaskList.add(
                        TaskDocument(
                            document["ID"] as String, document["title"] as String,
                            document["content"] as String, document["dueDateHumanReadable"] as String,
                            document["dueTimeHumanReadable"] as String, document["complete"] as Boolean,
                            document["dueDateTimeUnix"] as Double
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
        val taskItemPath = userdataBasePath.collection("task-data").document(taskID)
        taskItemPath.get().addOnSuccessListener { document ->
            val documentTemp = document.data!!
            documentTemp.let {
                it["complete"] = newValue
            }
            taskItemPath.set(documentTemp, SetOptions.merge())
        }
    }

    fun saveDocument(
        documentFields: Map<String, Any>,
        documentID: String,
        type: DocumentType
    ) {
        val noteOrTask = if (type == DocumentType.NOTE) "note-data" else "task-data"
        val documentPath = firebaseFirestore.collection("userdata")
            .document(firebaseAuth.currentUser!!.uid).collection(noteOrTask).document(documentID)
        documentPath.set(documentFields)
    }

    fun deleteDocument(documentID: String, documentType: DocumentType) {
        val noteOrTask = if (documentType == DocumentType.NOTE) "note-data" else "task-data"
        val documentPath = firebaseFirestore.collection("userdata")
            .document(firebaseAuth.currentUser!!.uid).collection(noteOrTask).document(documentID)
        documentPath.delete()
    }

}