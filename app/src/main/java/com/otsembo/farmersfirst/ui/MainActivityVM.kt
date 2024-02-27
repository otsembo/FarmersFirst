package com.otsembo.farmersfirst.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otsembo.farmersfirst.common.AppResource
import com.otsembo.farmersfirst.common.AppUiState
import com.otsembo.farmersfirst.data.repository.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for MainActivity responsible for handling UI state and actions.
 *
 * @param authRepository The repository for authentication.
 */
class MainActivityVM(
    private val authRepository: IAuthRepository,
) : ViewModel() {

    // MutableStateFlow to hold the UI state
    private val _mainActivityUiState = MutableStateFlow(MainActivityUiState())

    // StateFlow to expose the UI state
    val mainActivityUiState: StateFlow<MainActivityUiState> = _mainActivityUiState

    /**
     * Initializes the MainActivityVM and checks if the user is signed in.
     */
    init {
        handleActions(MainActivityActions.CheckIsSignedIn)
    }

    /**
     * Handles incoming actions and updates the UI state accordingly.
     *
     * @param action The action to handle.
     */
    fun handleActions(action: MainActivityActions) {
        when(action){
            MainActivityActions.CheckIsSignedIn -> checkSignInStatus()
            is MainActivityActions.SetWideScreenState -> setWideScreen(action.isWideScreen)
        }
    }

    /**
     * Checks if the user is signed in.
     * Updates the UI state accordingly.
     */
    private fun checkSignInStatus(){
        viewModelScope.launch {
            _mainActivityUiState.update { it.setLoading() }
            when(val signInRes = authRepository.checkIfSignedIn().last()){
                is AppResource.Success -> _mainActivityUiState.update {
                    it.reset().copy(isUserSignedIn = true)
                }
                is AppResource.Error -> _mainActivityUiState.update {
                    it.setError(signInRes.info)
                }
                is AppResource.Loading -> _mainActivityUiState.update {
                    it.setLoading()
                }
            }
        }
    }

    /**
     * Sets the wide screen flag in the UI state.
     *
     * @param isWideScreen Boolean indicating if the screen is wide.
     */
    private fun setWideScreen(isWideScreen: Boolean){
        _mainActivityUiState.update {
            it.copy(isWideScreen = isWideScreen)
        }
    }

}

/**
 * Sealed class representing actions for MainActivityVM.
 */
sealed class MainActivityActions {
    data object CheckIsSignedIn : MainActivityActions()
    data class SetWideScreenState(val isWideScreen: Boolean): MainActivityActions()
}

/**
 * Data class representing the UI state of MainActivity.
 *
 * @param isUserSignedIn Boolean indicating if the user is signed in.
 * @param isWideScreen Boolean indicating if the screen is wide.
 * @param isLoading Boolean indicating if data is being loaded.
 * @param errorOccurred Boolean indicating if an error occurred.
 * @param errorMessage String containing error message.
 */
data class MainActivityUiState(
    val isUserSignedIn: Boolean = false,
    val isWideScreen: Boolean = false,
    val isLoading: Boolean = false,
    val errorOccurred: Boolean = false,
    val errorMessage: String = ""
): AppUiState<MainActivityUiState> {

    /**
     * Resets the UI state.
     */
    override fun reset(): MainActivityUiState {
        return MainActivityUiState(
            isUserSignedIn, isWideScreen
        )
    }

    /**
     * Sets an error state in the UI.
     *
     * @param message The error message.
     */
    override fun setError(message: String): MainActivityUiState {
        return reset().copy(errorOccurred = true, errorMessage = message)
    }

    /**
     * Sets the loading state in the UI.
     */
    override fun setLoading(): MainActivityUiState {
        return reset().copy(isLoading =  true)
    }
}
