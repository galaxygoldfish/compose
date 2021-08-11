package com.compose.app.android.firebase

import androidx.lifecycle.MutableLiveData
import com.compose.app.android.model.NoteDocument
import com.compose.app.android.model.TaskDocument
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseDocument {

    private val firebaseFirestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth

    private val userdataBasePath = firebaseFirestore.collection("userdata")
        .document(firebaseAuth.currentUser!!.uid)

    fun getAllNotes(
        liveData: MutableLiveData<MutableList<NoteDocument>>,
        isUpdating: SwipeRefreshState
    ) {
        isUpdating.isRefreshing = true
        val noteCollectionPath = userdataBasePath.collection("note-data")
        noteCollectionPath.get().addOnSuccessListener { result ->
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

}