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


/**
 * Extension function for AppResource, used to coerce the type of the resource
 * @param action The transformation function to be applied to the resource
 * @return AppResource<T> The coerced resource with type T
 */
fun <T, R> AppResource<R>.coerceTo(action: (AppResource<R>) -> T): AppResource<T> {
    // Check the type of the current AppResource
    return when (this) {
        // If the current resource is an error, return a new AppResource.Error with the same error info
        is AppResource.Error -> AppResource.Error(info = info)
        // If the current resource is a loading state, return a new AppResource.Loading
        is AppResource.Loading -> AppResource.Loading()
        // If the current resource is a success, apply the provided transformation function to it
        // and wrap the result in a new AppResource.Success of type T
        is AppResource.Success -> AppResource.Success(result = action(this))
    }
}

/**
 * Interface representing the UI state of the application.
 *
 * @param T The type of the UI state.
 */
interface AppUiState<out T> {
    /**
     * Resets the UI status while keeping valid data already shown.
     *
     * @return The updated UI state after reset.
     */
    fun reset(): T
    fun setError(message: String): T
    fun setLoading(): T
}


fun Any?.izNull(): Boolean = this == null
fun Any?.notNull(): Boolean = this != null


