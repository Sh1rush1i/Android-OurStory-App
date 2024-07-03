package com.submis.ourstory.dom.story.usecase

import com.submis.ourstory.dom.story.model.StoryUploadRequest
import javax.inject.Inject

class StoryInteractor @Inject constructor(private val repository: StoryRepository) :
    StoryUseCase {

    override fun getStory() = repository.getStories()

    override fun getStoryLocation() = repository.getStoryLocation()

    override fun getDetailStory(id: String) = repository.getDetailStory(id)

    override fun uploadStory(story: StoryUploadRequest) = repository.uploadStory(story)
}