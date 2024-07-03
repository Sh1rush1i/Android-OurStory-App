package com.submis.ourstory.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.submis.ourstory.data.init.datastore.UserPreferences
import com.submis.ourstory.data.init.entity.RemoteKeys
import com.submis.ourstory.data.init.entity.StoryEntity
import com.submis.ourstory.data.init.room.StoryDatabase
import com.submis.ourstory.data.init.retrofit.ApiService
import com.submis.ourstory.utils.*
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKeyToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }


        return try {
            val token = userPreferences.getToken().first()
            val response = apiService.getStory(token.generateToken(), page, state.config.pageSize)
            val endOfPaginationReached = response.listStory.isEmpty()
            val storyEntities = response.listStory.toStoryEntity()

            database.withTransaction {
                val remoteKeysDao = database.remoteKeysDao()
                val storyDao = database.storyDao()

                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.deleteRemoteKeys()
                    storyDao.deleteAll()
                }

                val prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = response.listStory.map {
                    RemoteKeys(it.id, prevKey = prevKey, nextKey = nextKey)
                }
                remoteKeysDao.insertAll(keys)
                storyDao.insertStories(storyEntities)
            }

            MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }


    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }
            ?.data
            ?.lastOrNull()
            ?.let { data ->
                database.remoteKeysDao().getRemoteKeys(data.id)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }
            ?.data
            ?.firstOrNull()
            ?.let { data ->
                database.remoteKeysDao().getRemoteKeys(data.id)
            }
    }

    private suspend fun getClosestRemoteKeyToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition
            ?.let { position ->
                state.closestItemToPosition(position)
                    ?.id
                    ?.let { id ->
                        database.remoteKeysDao().getRemoteKeys(id)
                    }
            }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}