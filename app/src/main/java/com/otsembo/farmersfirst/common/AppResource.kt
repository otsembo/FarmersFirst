package com.otsembo.farmersfirst.common

/**
 * Sealed class representing different states or outcomes of an asynchronous operation.
 * @param data The result data associated with the operation. Can be null.
 * @param message The optional message associated with the state.
 */
sealed class AppResource<T>(val data: T? = null, private val message: String? = null) {

    /**
     * Represents the success state of an operation.
     * @param result The result data associated with the success state.
     */
    data class Success<T>(val result: T) : AppResource<T>(result)

    /**
     * Represents the error state of an operation.
     * @param info The error message or information associated with the error state.
     * @param result The result data associated with the error state. Can be null.
     */
    data class Error<T>(val info: String, val result: T? = null) : AppResource<T>(result, info)

    /**
     * Represents the loading state of an operation.
     * @param result The result data associated with the loading state. Can be null.
     */
    data class Loading<T>(val result: T? = null) : AppResource<T>(result)
}
