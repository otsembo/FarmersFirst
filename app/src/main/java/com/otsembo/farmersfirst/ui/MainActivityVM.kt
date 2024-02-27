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

class MainActivityVM(
    private val authRepository: IAuthRepository,
) : ViewModel() {

    private val _mainActivityUiState = MutableStateFlow(MainActivityUiState())
    val mainActivityUiState: StateFlow<MainActivityUiState> = _mainActivityUiState

    init {
        handleActions(MainActivityActions.CheckIsSignedIn)
    }

    fun handleActions(action: MainActivityActions) {
        when(action){
            MainActivityActions.CheckIsSignedIn -> checkSignInStatus()
            is MainActivityActions.SetWideScreenState -> setWideScreen(action.isWideScreen)
        }
    }

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

    private fun setWideScreen(isWideScreen: Boolean){
        _mainActivityUiState.update {
            it.copy(isWideScreen = isWideScreen)
        }
    }

}


sealed class MainActivityActions {
    data object CheckIsSignedIn : MainActivityActions()
    data class SetWideScreenState(val isWideScreen: Boolean): MainActivityActions()
}

data class MainActivityUiState(
    val isUserSignedIn: Boolean = false,
    val isWideScreen: Boolean = false,
    val isLoading: Boolean = false,
    val errorOccurred: Boolean = false,
    val errorMessage: String = ""
): AppUiState<MainActivityUiState> {

    override fun reset(): MainActivityUiState {
        return MainActivityUiState(
            isUserSignedIn, isWideScreen
        )
    }

    override fun setError(message: String): MainActivityUiState {
        return reset().copy(errorOccurred = true, errorMessage = message)
    }

    override fun setLoading(): MainActivityUiState {
        return reset().copy(isLoading =  true)
    }
}