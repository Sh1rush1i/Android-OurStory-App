package com.submis.ourstory.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.submis.ourstory.dom.story.usecase.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val useCase: StoryUseCase) : ViewModel() {

    private val storyId = MutableLiveData<String>()

    fun setStoryId(id: String) {
        storyId.value = id
    }

    val detailStory by lazy {
        storyId.switchMap {
            useCase.getDetailStory(it).asLiveData()
        }
    }
}