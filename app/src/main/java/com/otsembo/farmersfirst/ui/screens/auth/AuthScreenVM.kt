package com.otsembo.farmersfirst.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.data.repository.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing the authentication screen state.
 *
 * @param application Instance of the application.
 * @param authRepository Repository interface for authentication operations.
 */
class AuthScreenVM(
    application: Application,
    private val authRepository: IAuthRepository
) : AndroidViewModel(application) {

    // Mutable state flow to represent the authentication UI state
    private val _authUiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState

    init {
        // Checking if the user is signed in when ViewModel is initialized
        handleAuthActions(AuthActions.CheckIfSignedIn)
    }

    /**
     * Function to handle different authentication actions.
     *
     * @param action The authentication action to be handled.
     */
    fun handleAuthActions(action: AuthActions) {
        when (action) {
            AuthActions.RequestSignIn -> {
                // Performing user sign-in
                viewModelScope.launch {
                    authRepository.signInUser().collect { auth ->
                        when (auth) {
                            is AppResource.Success -> handleAuthActions(AuthActions.SignInComplete)
                            else -> _authUiState.update { it.copy(isSignedIn = false) }
                        }
                    }
                }
            }
            AuthActions.SignInComplete -> {
                // Updating UI state after successful sign-in
                _authUiState.update { it.copy(isSignedIn = true) }
            }
            AuthActions.CheckIfSignedIn -> {
                // Checking if the user is signed in
                viewModelScope.launch {
                    authRepository.checkIfSignedIn().collectLatest { isSignedIn ->
                        when (isSignedIn) {
                            is AppResource.Success -> handleAuthActions(AuthActions.SignInComplete)
                            else -> _authUiState.update { it.copy(isSignedIn = false) }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "AuthScreenVM"
    }
}

/**
 * Data class representing the state of the authentication UI.
 *
 * @property isSignedIn Boolean indicating whether the user is signed in or not.
 */
data class AuthUiState(
    val isSignedIn: Boolean = false
)

/**
 * Sealed class representing different authentication actions.
 */
sealed class AuthActions {
    data object RequestSignIn : AuthActions()
    data object SignInComplete : AuthActions()
    data object CheckIfSignedIn : AuthActions()
}
