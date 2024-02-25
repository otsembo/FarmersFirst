package com.otsembo.farmersfirst.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File

/**
 * Abstract base class for implementing preference repositories using Android's DataStore.
 * Provides methods for adding, fetching, and removing data from the DataStore.
 */
abstract class BasePreferenceRepository {

    /**
     * The DataStore instance used for storing preferences.
     */
    abstract val dataStore: DataStore<Preferences>

    /**
     * Adds data to the DataStore with the specified key and value.
     *
     * @param key The key for the data to be added.
     * @param value The value to be added.
     * @return The updated Preferences after adding the data.
     */
    suspend fun <T> addData(key: Preferences.Key<T>, value: T): Preferences {
        return dataStore.edit { mutablePreferences -> mutablePreferences[key] = value }
    }

    /**
     * Fetches data from the DataStore with the specified key, or returns the default value if not found.
     *
     * @param key The key for the data to be fetched.
     * @param default The default value to return if the data is not found.
     * @return The fetched data or the default value.
     */
    suspend fun <T> fetchData(key: Preferences.Key<T>, default: T): T {
        return dataStore.data.map { preferences -> preferences[key] ?: default }.first()
    }

    /**
     * Removes data from the DataStore with the specified key.
     *
     * @param key The key for the data to be removed.
     */
    suspend fun <T> removeData(key: Preferences.Key<T>) {
        dataStore.edit { mutablePreferences -> mutablePreferences.remove(key) }
    }

    companion object {
        /**
         * Builds a DataStore instance with the specified store name.
         *
         * @param storeName The name of the DataStore.
         * @return The built DataStore instance.
         */
        fun Context.buildStore(storeName: String): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                corruptionHandler = null,
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            ) {
                File(filesDir, "datastore/$storeName.preferences_pb")
            }
        }
    }
}
