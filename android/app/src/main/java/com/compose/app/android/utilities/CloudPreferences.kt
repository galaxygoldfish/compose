package com.compose.app.android.utilities

import android.content.Context
import android.util.Log
import com.compose.app.android.firebase.FirebaseDocument
import com.compose.app.android.model.DocumentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** SharedPreferences, but synced with Firebase! **/
class CloudPreferences(context: Context) {

    private val sharedPreferencesDefault = context.let { it.getSharedPreferences(it.packageName, 0) }

    /**
     * Synchronize all values from the cloud locally, to
     * get the most recent user settings that might have
     * been changed on other devices. Call this function
     * often to make sure that all values are up to date,
     * usually on app startup or after saving a document.
     */
    fun synchronizeLocal() {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseDocument().getPreferenceMap().apply {
                // keys forEach to account for API levels 24 to 21
                keys.forEach { key ->
                    this[key]?.let { putDataLocal(key, it) }
                }
            }
        }
    }

    /**
     * Save a boolean value
     *
     * @param key - The key to be used to retrieve the item
     * @param value - The value to save
     */
    fun putBoolean(key: String, value: Boolean) {
        saveItem(key, value)
    }

    /**
     * Retrieve a boolean value
     *
     * @param key - Key used when saving the item
     * @param defaultValue - If the key is incorrect or the
     * value can't be found, return this value
     */
    fun getBoolean(key: String, defaultValue: Boolean? = false) : Boolean {
        return (getItemLocal(key, defaultValue) ?: defaultValue) as Boolean
    }

    /**
     * Save a string value
     *
     * @param key - The key to be used to retrieve the item
     * @param value - The value to save
     */
    fun putString(key: String, value: String) {
        saveItem(key, value)
    }

    /**
     * Retrieve a string value
     *
     * @param key - Key used when saving the item
     * @param defaultValue - If the key is incorrect or the
     * value can't be found, return this value
     */
    fun getString(key: String, defaultValue: String? = "") : String {
        return (getItemLocal(key, defaultValue) ?: defaultValue) as String
    }

    /**
     * Save an integer value
     *
     * @param key - The key to be used to retrieve the item
     * @param value - The value to save
     */
    fun putInteger(key: String, value: Int) {
        saveItem(key, value)
    }

   /**
    * Retrieve an integer value
    *
    * @param key - Key used when saving the item
    * @param defaultValue - If the key is incorrect or the
    * value can't be found, return this value
    */
    fun getInteger(key: String, defaultValue: Int? = 0) : Int {
        return (getItemLocal(key, defaultValue) ?: defaultValue) as Int
    }

    private fun getItemLocal(key: String, defaultValue: Any?) : Any? {
        Log.e("COMPOSE", "getItemLocal $key")
        return when (defaultValue) {
            is Int -> sharedPreferencesDefault.getInt(key, defaultValue)
            is String -> sharedPreferencesDefault.getString(key, defaultValue)
            is Boolean -> sharedPreferencesDefault.getBoolean(key, defaultValue)
            else -> Any()
        }
    }

    /**
     * Sync the value both locally and remote afterwards
     */
    private fun saveItem(key: String, item: Any) {
        putDataLocal(key, item)
        CoroutineScope(Dispatchers.IO).launch {
            putDataRemote(key, item)
        }
    }

    /**
     * Save the value to SharedPreferences locally
     */
    private fun putDataLocal(key: String, value: Any) {
        Log.e("COMPOSE", "putDataLocal")
        sharedPreferencesDefault.edit().apply {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
            }
            apply()
        }
    }

    /**
     * Upload the value to Firebase on the preference
     * document for the current user
     */
    private fun putDataRemote(key: String, value: Any) {
        Log.e("COMPOSE", "putDataRemote")
        FirebaseDocument().apply {
            updateSpecificValue(
                key = key,
                newValue = value,
                documentType = DocumentType.PREFERENCE
            )
        }
    }

}

/**
 * Get an instance of the cloud preference manager.
 * Context is needed to call this function
 */
fun Context.getCloudPreferences() : CloudPreferences = CloudPreferences(this)