package com.otsembo.farmersfirst.data.repository

import com.otsembo.farmersfirst.common.AppResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

/**
 * Base repository class for performing database transactions.
 * This class utilizes Kotlin coroutines and flows to handle asynchronous operations.
 */
abstract class BaseRepository {

    /**
     * Executes a database transaction asynchronously and emits the result as a flow of AppResource.
     * @param flow The flow representing the database transaction.
     * @return A flow of AppResource representing the transaction result.
     */
    suspend fun <T> dbTransact(flow: Flow<T>) = flow {
        // Emit loading state
        emit(AppResource.Loading())

        // Collect the flow and emit success state with the result
        flow.collectLatest {
            emit(AppResource.Success(result = it))
        }
    }.catch { error ->
        // Catch any errors during the transaction and emit error state
        emit(AppResource.Error(info = error.message ?: "An error occurred!"))
    }
}
