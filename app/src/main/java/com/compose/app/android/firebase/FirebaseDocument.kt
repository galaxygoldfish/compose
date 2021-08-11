package com.compose.app.android.firebase

import androidx.lifecycle.MutableLiveData
import com.compose.app.android.model.NoteDocument
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseDocument {

    private val firebaseFirestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth

    fun getAllNotes(liveData: MutableLiveData<MutableList<NoteDocument>>, isUpdating: SwipeRefreshState) {
        isUpdating.isRefreshing = true
        val noteCollectionPath = firebaseFirestore.collection("userdata")
            .document(firebaseAuth.currentUser!!.uid).collection("note-data")
        noteCollectionPath.get().addOnSuccessListener { result ->
            liveData.value = mutableListOf()
            result.documents.forEach { documentSnapshot ->
                val noteData = documentSnapshot.data
                noteData?.let { document ->
                    val liveDataTemp = liveData.value as MutableList<NoteDocument>
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

}