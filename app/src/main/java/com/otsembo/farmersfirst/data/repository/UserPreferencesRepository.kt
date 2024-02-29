package com.otsembo.farmersfirst.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.otsembo.farmersfirst.common.AppResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Interface for the user preferences repository, defining methods for managing user preferences.
 */
interface IUserPrefRepository {
    /**
     * Adds the user token to the preferences store.
     * @param token The user token to be added.
     * @param userId The ID of the user associated with the token.
     * @return A flow of AppResource indicating the success or failure of the operation.
     */
    suspend fun addUserToStore(
        token: String,
        userId: Int,
    ): Flow<AppResource<Boolean>>

    /**
     * Removes the user from the preferences store.
     * @return A flow of AppResource indicating the success or failure of the operation.
     */
    suspend fun removeUserFromStore(): Flow<AppResource<Boolean>>

    /**
     * Fetches the user token from the preferences store.
     * @return A flow of AppResource containing the user token or an error message.
     */
    suspend fun fetchToken(): Flow<AppResource<String>>

    /**
     * Fetches the user ID from the preferences store.
     * @return A flow of AppResource containing the user ID or an error message.
     */
    suspend fun fetchId(): Flow<AppResource<Int>>
}

/**
 * Repository class for managing user preferences.
 * This class implements the IUserPrefRepository interface and extends the BasePreferenceRepository class.
 *
 * @param context The application context.
 */
class UserPreferencesRepository(context: Context) : IUserPrefRepository, BasePreferenceRepository() {
    override val dataStore: DataStore<Preferences> = context.buildStore(STORE_NAME)

    override suspend fun addUserToStore(
        token: String,
        userId: Int,
    ): Flow<AppResource<Boolean>> =
        flow {
            emit(AppResource.Loading())
            addData(userTokenPreferenceKey, token)
            addData(userIdPreferenceKey, userId)
            emit(AppResource.Success(result = true))
        }.catch { emit(AppResource.Error(info = it.message ?: "An error occurred")) }

    override suspend fun removeUserFromStore(): Flow<AppResource<Boolean>> =
        flow {
            emit(AppResource.Loading())
            removeData(userTokenPreferenceKey)
            removeData(userIdPreferenceKey)
            emit(AppResource.Success(result = true))
        }.catch { emit(AppResource.Error(info = it.message ?: "An error occurred")) }

    override suspend fun fetchToken(): Flow<AppResource<String>> =
        flow {
            emit(AppResource.Loading())
            val tokenValue = fetchData(userTokenPreferenceKey, default = DEFAULT_VALUE)
            if (tokenValue == DEFAULT_VALUE) {
                emit(AppResource.Error(info = "Could not fetch token"))
            } else {
                emit(AppResource.Success(result = tokenValue))
            }
        }.catch { emit(AppResource.Error(it.message ?: "Could not retrieve token")) }

    override suspend fun fetchId(): Flow<AppResource<Int>> =
        flow {
            emit(AppResource.Loading())
            val idValue = fetchData(userIdPreferenceKey, default = 0)
            if (idValue == 0) {
                emit(AppResource.Error(info = "Could not fetch user ID"))
            } else {
                emit(AppResource.Success(result = idValue))
            }
        }.catch { emit(AppResource.Error(info = it.message ?: "Could not fetch user ID")) }

    companion object {
        const val STORE_NAME = "user_data"
        private const val DEFAULT_VALUE = "non_value"
        private val userTokenPreferenceKey = stringPreferencesKey("user_token")
        private val userIdPreferenceKey = intPreferencesKey("user_id")
    }
}
