package com.submis.ourstory.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.auth.model.Register
import com.submis.ourstory.dom.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val useCase: AuthUseCase) : ViewModel() {

    private val registerComplete = MutableLiveData<Result<Register>>()
    val registerResult: LiveData<Result<Register>>
        get() = registerComplete

    fun invokeRegister(name: String, email: String, password: String) = viewModelScope.launch {
        useCase.invokeRegister(name, email, password).collect { result ->
            registerComplete.value = result
        }
    }
}