package com.submis.ourstory.ui.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.submis.ourstory.dom.auth.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashscreenViewModel @Inject constructor(private val useCase: AuthUseCase) : ViewModel() {

    fun getStatus() = useCase.getStatus().asLiveData()
}