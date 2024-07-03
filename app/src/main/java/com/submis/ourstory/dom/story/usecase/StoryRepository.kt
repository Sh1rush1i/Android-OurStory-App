package com.submis.ourstory.dom.story.usecase

import androidx.paging.PagingData
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.story.model.Story
import com.submis.ourstory.dom.story.model.StoryUpload
import com.submis.ourstory.dom.story.model.StoryUploadRequest
import kotlinx.coroutines.flow.Flow

interface StoryRepository {

    fun getStories(): Flow<PagingData<Story>>

    fun getStoryLocation(): Flow<Result<List<Story>>>

    fun getDetailStory(id: String): Flow<Result<Story>>

    fun uploadStory(story: StoryUploadRequest): Flow<Result<StoryUpload>>

}