package com.submis.ourstory.ui.main.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.story.model.StoryUpload
import com.submis.ourstory.dom.story.model.StoryUploadRequest
import com.submis.ourstory.dom.story.usecase.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadVIewModel @Inject constructor(private val useCase: StoryUseCase) : ViewModel() {

    private val resultUpload = MutableLiveData<Result<StoryUpload>>()
    val uploadStoryResult: LiveData<Result<StoryUpload>>
        get() = resultUpload

    fun uploadStory(image: File, description: String, location: Location? = null) = viewModelScope.launch {
        val storyRequest = StoryUploadRequest(image, description, location)
        useCase.uploadStory(storyRequest).collect { result ->
            resultUpload.value = result
        }
    }
}