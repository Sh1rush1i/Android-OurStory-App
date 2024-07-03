package com.submis.ourstory.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.submis.ourstory.dom.story.model.Story
import com.submis.ourstory.dom.auth.usecase.AuthUseCase
import com.submis.ourstory.dom.story.usecase.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val storyUseCase: StoryUseCase,
) : ViewModel() {

    val stories = MutableLiveData<PagingData<Story>>()

    init {
        loadNewStory()
    }

    fun loadNewStory() = viewModelScope.launch {
        storyUseCase.getStory().cachedIn(viewModelScope).collect { story ->
            stories.value = story
        }
    }

    fun logOut() = viewModelScope.launch {
        authUseCase.wipeUserAuthorization()
    }
}