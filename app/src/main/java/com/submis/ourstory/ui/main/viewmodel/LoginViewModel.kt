package com.submis.ourstory.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.auth.model.Login
import com.submis.ourstory.dom.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val useCase: AuthUseCase) : ViewModel() {

    private val loginOutput = MutableSharedFlow<Result<Login>>()
    val loginResult: SharedFlow<Result<Login>>
        get() = loginOutput

    fun login(email: String, password: String) = viewModelScope.launch {
        useCase.invokeLogin(email, password).collect { result ->
            loginOutput.emit(result)
        }
    }
}