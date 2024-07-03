package com.submis.ourstory.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.story.model.Story
import com.submis.ourstory.dom.story.usecase.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val useCase: StoryUseCase) : ViewModel() {

    private val userStory = MutableLiveData<Result<List<Story>>>()
    val stories: LiveData<Result<List<Story>>>
        get() = userStory

    fun getStoryLocation() = viewModelScope.launch {
        useCase.getStoryLocation().collect { result ->
            userStory.value = result
        }
    }
}