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

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.Patterns
import androidx.core.net.toUri
import com.compose.app.android.R
import com.compose.app.android.utilities.getDefaultPreferences
import com.compose.app.android.utilities.rawStringResource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class FirebaseAccount {

    private val firebaseAuth: FirebaseAuth = Firebase.auth
    private val firebaseFirestore: FirebaseFirestore = Firebase.firestore
    private val firebaseStorage: FirebaseStorage = Firebase.storage

    /**
     * Check whether there is currently an authenticated user signed-in.
     * @return A boolean value indicating true if a user is logged in, and
     * false if there are no users logged in.
     */
    fun determineIfUserExists(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * Sign-in an existing user and download corresponding user metadata,
     * including profile image & name.
     * @param email - User-provided email address that is linked to their
     * account
     * @param password - User-provided password string that they have set
     * for their account
     * @param context - Activity or lifecycle owner needed to access
     * SharedPreferences -> TODO Migration to DataStore
     * @return A boolean value indicating whether the authentication attempt
     * was successful or not.
     */
    suspend fun authenticateWithEmail(
        email: String,
        password: String,
        context: Context
    ): Boolean {
        val completableToken = CompletableDeferred<Boolean>()
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        val sharedPreferences = context.getDefaultPreferences()
        if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() && password.isNotEmpty()
        ) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                asyncScope.launch {
                    getUserMetadata().let {
                        if (sendProfileImageToFile(context.filesDir.path)) {
                            sharedPreferences.edit().apply {
                                putString("IDENTITY_USER_NAME_FIRST", it["FIRST-NAME"] as String?)
                                putString("IDENTITY_USER_NAME_LAST", it["LAST-NAME"] as String?)
                                putString("IDENTITY_USER_AUTHENTICATOR", password)
                            }.apply()
                            completableToken.complete(true)
                        } else {
                            firebaseAuth
                            completableToken.complete(false)
                        }
                    }
                }
            }.addOnFailureListener {
                completableToken.complete(false)
            }
        } else {
            completableToken.complete(false)
        }
        return completableToken.await()
    }

    /**
     * Create a new user with e-mail and password and associate related data like
     * avatar image and name with the account.
     * @param email - User-provided email address that is to be linked to the new
     * account
     * @param password - User-provided password string that exceeds 3 characters in
     * length and is used to re-authenticate the user.
     * @param firstName - The user's first name
     * @param lastName - The user's last name
     * @param profileImage - User-chosen avatar image in bitmap format to be uploaded
     * to Firebase storage
     * @param context - Activity or lifecycle owner needed to access string resources
     * and SharedPreferences -> TODO Migration to DataStore
     * @return A boolean value indicating that the user's account was created, the
     * avatar was uploaded, and all metadata was successfully inserted into Firebase.
     */
    suspend fun createNewAccount(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        profileImage: Bitmap,
        context: Context,
    ): String {
        val completableToken = CompletableDeferred<String>()
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        val sharedPreferences = context.getDefaultPreferences()
        if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (password.isNotEmpty() && password.length >= 3) {
                if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val userdataMap =
                                hashMapOf("FIRST-NAME" to firstName, "LAST-NAME" to lastName)
                            asyncScope.launch {
                                if (uploadNewUserMetadata(userdataMap)) {
                                    sharedPreferences.edit().apply {
                                        putString("IDENTITY_USER_NAME_FIRST", firstName)
                                        putString("IDENTITY_USER_NAME_LAST", lastName)
                                        putString("IDENTITY_USER_AUTHENTICATOR", password)
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

    /**
     * Insert updated user metadata to Firebase, including the user's
     * first and last name.
     * @param mapData - A map containing two key-value pairs: firstName
     * and lastName, each corresponding with the user's first or last name
     * @return A boolean value indicating whether the user metadata has
     * been updated/inserted without a failure or exception.
     */
    suspend fun uploadNewUserMetadata(
        mapData: Map<String, String>
    ): Boolean {
        val completableToken = CompletableDeferred<Boolean>()
        val userMetadataPath = firebaseFirestore.collection("METADATA")
            .document("USERS").collection(firebaseAuth.currentUser!!.uid)
            .document("USERFILE")
        userMetadataPath.set(mapData).addOnSuccessListener {
            completableToken.complete(true)
        }.addOnFailureListener {
            Log.e("COMPOSE", "${it.message}\n\n\n${it.stackTrace}\n\n\n${it.cause}")
            completableToken.complete(false)
        }
        return completableToken.await()
    }

    /**
     * Upload a new avatar image for the user, replacing the old one
     * automatically if it exists.
     * @param avatarImage - The new profile image to be uploaded and
     * set, in bitmap format.
     * @param context - Activity or lifecycle owner needed to access
     * app-specific filesystems.
     * @return A boolean value indicating whether the avatar has been
     * uploaded to Firebase without any error.
     */
    suspend fun uploadNewProfileImage(
        avatarImage: Bitmap,
        context: Context,
    ): Boolean {
        val completableToken = CompletableDeferred<Boolean>()
        val asyncScope = CoroutineScope(Dispatchers.IO + Job())
        val avatarImagePath =
            firebaseStorage.reference.child("USER-AVATARS/${firebaseAuth.currentUser!!.uid}")
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

    /**
     * Fetch associated user metadata, including first and last name,
     * from firebase.
     * @return A map containing two key-value pairs, including firstName
     * and lastName, as strings.
     */
    suspend fun getUserMetadata(): Map<*, *> {
        val completableToken = CompletableDeferred<Map<*, *>>()
        val userMetadataPath = firebaseFirestore.collection("METADATA")
            .document("USERS").collection(firebaseAuth.currentUser!!.uid)
            .document("USERFILE")
        userMetadataPath.get().addOnSuccessListener {
            completableToken.complete(it.data as Map<*, *>)
        }.addOnFailureListener {
            val errorMap = hashMapOf("FIRST-NAME" to "Error", "LAST-NAME" to "Error")
            completableToken.complete(errorMap)
        }
        return completableToken.await()
    }

    /**
     * Send the most updated avatar image to the recognized file "avatar.png".
     * This can be used to ensure that avatars are synced across all logged-in
     * devices, and also so that other parts of the app can access the most
     * recent version of the user's avatar.
     * @param filesDirPath - App-specific files directory where the avatar image
     * is to be stored (usually context.filesDir, but by providing the raw path, no context
     * is needed)
     * @return A boolean value indicating whether the avatar was sent to the
     * file successfully.
     */
    suspend fun sendProfileImageToFile(
        filesDirPath: String
    ): Boolean {
        val completableToken = CompletableDeferred<Boolean>()
        val avatarImagePath =
            firebaseStorage.reference.child("USER-AVATARS/${firebaseAuth.currentUser!!.uid}")
        val localImagePath = File("${filesDirPath}/avatar.png")
        val storageDocumentPath = firebaseFirestore.collection("METADATA").document("USERS")
            .collection(firebaseAuth.currentUser!!.uid).document("QUOTA-MONITOR")
        avatarImagePath.getFile(localImagePath).addOnSuccessListener {
            storageDocumentPath.set(
                mapOf("USER-AVATAR" to localImagePath.length()),
                SetOptions.merge()
            )
            completableToken.complete(true)
        }.addOnFailureListener {
            completableToken.complete(false)
        }
        return completableToken.await()
    }

}