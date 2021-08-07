package com.compose.app.android.account

import android.content.Context
import android.graphics.Bitmap
import android.util.Patterns
import androidx.core.net.toUri
import com.compose.app.android.R
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.utilities.rawStringResource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FirebaseAccount {

    private val firebaseAuth: FirebaseAuth = Firebase.auth
    private val firebaseFirestore: FirebaseFirestore = Firebase.firestore
    private val firebaseStorage: FirebaseStorage = Firebase.storage

    fun determineIfUserExists() : Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun authenticateWithEmail(email: String, password: String, context: Context) : Boolean {
        val completableToken = CompletableDeferred<Boolean>()
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        val sharedPreferences = context.getDefaultPreferences()
        if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                asyncScope.launch {
                    getUserMetadata().let {
                        sharedPreferences.edit().apply {
                            putString("IDENTITY_USER_NAME_FIRST", it["firstName"] as String?)
                            putString("IDENTITY_USER_NAME_LAST", it["lastName"] as String)
                        }.apply()
                    }
                    completableToken.complete(true)
                }
            }.addOnFailureListener {
                completableToken.complete(false)
            }
        } else {
            completableToken.complete(false)
        }
        return completableToken.await()
    }

    suspend fun createNewAccount(email: String, password: String, firstName: String, lastName: String,
                                 profileImage: Bitmap, context: Context) : String {
        val completableToken = CompletableDeferred<String>()
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        val sharedPreferences = context.getDefaultPreferences()
        if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (password.isNotEmpty() && password.length >= 3) {
                if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                        val userdataMap = hashMapOf("firstName" to firstName, "lastName" to lastName)
                        asyncScope.launch {
                            if (uploadNewUserMetadata(userdataMap)) {
                                sharedPreferences.edit().apply {
                                    putString("IDENTITY_USER_NAME_FIRST", firstName)
                                    putString("IDENTITY_USER_NAME_LAST", lastName)
                                }.apply()
                                if (uploadNewProfileImage(profileImage, context)) {
                                    completableToken.complete("true")
                                } else completableToken.complete(context.rawStringResource(R.string.create_account_failure_generic))
                            } else completableToken.complete(context.rawStringResource(R.string.create_account_failure_generic))
                        }
                    }.addOnFailureListener {
                        completableToken.complete(context.rawStringResource(R.string.create_account_failure_generic))
                    }
                } else completableToken.complete(context.rawStringResource(R.string.create_account_failure_name))
            } else completableToken.complete(context.rawStringResource(R.string.create_account_failure_password))
        } else completableToken.complete(context.rawStringResource(R.string.create_account_failure_email))
        return completableToken.await()
    }

    suspend fun uploadNewUserMetadata(mapData: Map<String, String>) : Boolean {
        val completableToken = CompletableDeferred<Boolean>()
        val userMetadataPath = firebaseFirestore.collection("metadata").document(firebaseAuth.currentUser!!.uid)
        userMetadataPath.set(mapData).addOnSuccessListener {
            completableToken.complete(true)
        }.addOnFailureListener {
            completableToken.complete(false)
        }
        return completableToken.await()
    }

    suspend fun uploadNewProfileImage(avatarImage: Bitmap, context: Context) : Boolean {
        val completableToken = CompletableDeferred<Boolean>()
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        val avatarImagePath = firebaseStorage.reference.child("metadata/avatars/${firebaseAuth.currentUser!!.uid}")
        val avatarImageLocal = File(context.filesDir, "avatar.png")
        asyncScope.launch {
            val fileOutputStream = FileOutputStream(avatarImageLocal)
            avatarImageLocal.createNewFile()
            avatarImage.compress(Bitmap.CompressFormat.PNG, 40, fileOutputStream)
            fileOutputStream.apply {
                flush(); close()
            }
            avatarImagePath.putFile(avatarImageLocal.toUri()).addOnSuccessListener {
                completableToken.complete(true)
            }.addOnFailureListener {
                completableToken.complete(false)
            }
        }
        return completableToken.await()
    }

    suspend fun getUserMetadata() : Map<*, *> {
        val completableToken = CompletableDeferred<Map<*, *>>()
        val userMetadataPath = firebaseFirestore.collection("metadata").document(firebaseAuth.currentUser!!.uid)
        userMetadataPath.get().addOnSuccessListener {
            completableToken.complete(it.data as Map<*, *>)
        }.addOnFailureListener {
            val errorMap = hashMapOf("firstName" to "Error", "lastName" to "Error")
            completableToken.complete(errorMap)
        }
        return completableToken.await()
    }

}