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

class AuthScreenVM(
    application: Application,
    private val authRepository: IAuthRepository): AndroidViewModel(application) {

        private val _authUiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())
        val authUiState: StateFlow<AuthUiState> = _authUiState

        init {
            handleAuthActions(AuthActions.CheckIfSignedIn)
        }

        fun handleAuthActions(action: AuthActions){
            when(action){
                AuthActions.RequestSignIn -> {
                    viewModelScope.launch {
                        authRepository.signInUser().collect { auth ->
                            when(auth){
                                is AppResource.Error -> {
                                    _authUiState.update { it.copy(isSignedIn = false) }
                                    println("$TAG: ${auth.info}")
                                }
                                is AppResource.Loading -> {
                                    _authUiState.update { it.copy(isSignedIn = false) }
                                }
                                is AppResource.Success -> {
                                    _authUiState.update { it.copy(isSignedIn = true) }
                                }
                            }
                        }
                    }
                }
                AuthActions.SignInComplete -> TODO()
                AuthActions.CheckIfSignedIn -> {
                    viewModelScope.launch {
                        authRepository.checkIfSignedIn().collectLatest { isSignedIn ->
                            when(isSignedIn){
                                is AppResource.Success -> _authUiState.update { it.copy(isSignedIn = true) }
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

data class AuthUiState(
    val isSignedIn: Boolean = false
)

sealed class AuthActions{
    data object RequestSignIn: AuthActions()
    data object SignInComplete: AuthActions()
    data object CheckIfSignedIn: AuthActions()
}