package com.submis.ourstory.data

import androidx.paging.*
import com.submis.ourstory.data.StoryRemoteMediator
import com.submis.ourstory.data.init.datastore.UserPreferences
import com.submis.ourstory.data.init.room.StoryDatabase
import com.submis.ourstory.data.init.RemoteDataSource
import com.submis.ourstory.data.init.retrofit.ApiService
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.story.model.StoryUploadRequest
import com.submis.ourstory.dom.story.usecase.StoryRepository
import com.submis.ourstory.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val userPreferences: UserPreferences,
) : StoryRepository {

    override fun getStories() =
        @OptIn(ExperimentalPagingApi::class)
        Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = false,
                initialLoadSize = 30
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreferences),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
        ).flow
            .map { pagingData ->
                toStoryDomain(pagingData)
            }

    override fun getStoryLocation() = flow {
        emit(Result.Loading())

        val token = try {
            userPreferences.getToken().first()
        } catch (e: Exception) {
            emit(Result.Error("Failed to retrieve token"))
            return@flow
        }

        try {
            val response = remoteDataSource.getStory(token.generateToken(), location = 1)
            val storyList = response.listStory.toStoryDomain()
            emit(Result.Success(storyList))
        } catch (e: IOException) {
            emit(Result.Error("Network error: ${e.message}"))
        } catch (e: HttpException) {
            emit(Result.Error("HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.message ?: "Unknown error"}"))
        }
    }.flowOn(Dispatchers.IO)


    override fun getDetailStory(id: String) = flow {
        emit(Result.Loading())

        val token = try {
            userPreferences.getToken().first()
        } catch (e: Exception) {
            emit(Result.Error("Failed to retrieve token"))
            return@flow
        }

        try {
            val response = remoteDataSource.getDetailStory(token.generateToken(), id)
            val storyDetail = response.story.toStoryDomain()
            emit(Result.Success(storyDetail))
        } catch (e: IOException) {
            emit(Result.Error("Network error: ${e.message}"))
        } catch (e: HttpException) {
            emit(Result.Error("HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error("Unexpected error: ${e.message ?: "Unknown error"}"))
        }
    }.flowOn(Dispatchers.IO)


    override fun uploadStory(story: StoryUploadRequest) = flow {
        emit(Result.Loading())

        withIdlingResource {
            val token = try {
                userPreferences.getToken().first()
            } catch (e: Exception) {
                emit(Result.Error("Failed to retrieve token"))
                return@withIdlingResource
            }

            try {
                val response = remoteDataSource.uploadStory(token.generateToken(), story)
                val result = response.toStoryUploadDomain()
                emit(Result.Success(result))
            } catch (e: IOException) {
                emit(Result.Error("Network error: ${e.message}"))
            } catch (e: HttpException) {
                emit(Result.Error("HTTP error: ${e.message}"))
            } catch (e: Exception) {
                emit(Result.Error("Unexpected error: ${e.message ?: "Unknown error"}"))
            }
        }
    }.flowOn(Dispatchers.IO)

}