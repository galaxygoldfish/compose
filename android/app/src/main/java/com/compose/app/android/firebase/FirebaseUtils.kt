package com.compose.app.android.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred

class FirebaseUtils {
    companion object {

        /**
         * Get the size, in bytes of a document in firebase.
         * @return - The number of bytes the document is counted
         * as under firebase firestore.
         * @param document - The target DocumentReference to be analyzed
         * for it's size
         */
        suspend fun calculateDocumentSize(document: DocumentReference): Int {
            val completableToken = CompletableDeferred<Int>()
            var totalDocumentSize = 32
            totalDocumentSize += document.path.length + 17 // keeping the "/" separator as it can count as the + 1 extra byte per string
            document.get()
                .addOnSuccessListener {
                    it.data?.keys?.forEach { key ->
                        totalDocumentSize += key.length + 1
                    }
                    it.data?.values?.forEach { value ->
                        totalDocumentSize += when (value) {
                            is Int -> 8
                            is Long -> 16 // estimate ?
                            is Boolean -> 1
                            is String -> value.length + 1
                            else -> 0
                        }
                    }
                    completableToken.complete(totalDocumentSize)
                }
                .addOnFailureListener {
                    completableToken.complete(32)
                }
            return completableToken.await()
        }

        /**
         * Fetch the total number of used bytes in firebase by the
         * current user.
         * @return - An int value containing the number of bytes that
         * the user has used in total, containing the profile picture,
         * and all notes and tasks.
         */
        suspend fun calculateUserStorage(): Int {
            val quotaDocument = Firebase.firestore.collection("METADATA").document("USERS")
                .collection(Firebase.auth.currentUser!!.uid).document("QUOTA-MONITOR")
            val completableToken = CompletableDeferred<Int>()
            quotaDocument.get().addOnSuccessListener {
                var storageCounter = 0
                it.data?.values?.forEach { value ->
                    storageCounter += (value as Long).toInt()
                }
                completableToken.complete(storageCounter)
            }
            return completableToken.await()
        }
    }
}