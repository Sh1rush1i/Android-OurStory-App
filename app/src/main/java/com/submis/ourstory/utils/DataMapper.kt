package com.submis.ourstory.utils

import androidx.paging.PagingData
import androidx.paging.map
import com.submis.ourstory.data.init.entity.StoryEntity
import com.submis.ourstory.data.init.response.*
import com.submis.ourstory.dom.auth.model.*
import com.submis.ourstory.dom.story.model.*

fun RegisterResponse?.toRegisterDomain() = Register(this?.error, this?.message)

fun LoginResponse?.toLoginDomain() = Login(this?.loginResult?.name, this?.loginResult?.userId, this?.loginResult?.token)

fun List<StoryItem>.toStoryDomain() = map {
    Story(it.id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon)
}

fun StoryItem.toStoryDomain() = Story(id, name, photoUrl, createdAt, description, lat, lon)

fun StoryUploadResponse.toStoryUploadDomain() = StoryUpload(error, message)

fun List<StoryItem>.toStoryEntity() = map {
    StoryEntity(it.id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon)
}

fun toStoryDomain(story: PagingData<StoryEntity>) = story.map {
    Story(it.id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon)
}