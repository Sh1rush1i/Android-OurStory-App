package com.submis.ourstory.data.init

import com.submis.ourstory.data.init.response.StoryUploadResponse
import com.submis.ourstory.data.init.retrofit.ApiService
import com.submis.ourstory.dom.story.model.StoryUploadRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun registUser(name: String, email: String, password: String) =
        apiService.registerUser(name, email, password)

    suspend fun loginUser(email: String, password: String) =
        apiService.loginUser(email, password)

    suspend fun getStory(token: String, page: Int? = null, size: Int? = null, location: Int = 0) =
        apiService.getStory(token, page, size, location)

    suspend fun getDetailStory(token: String, id: String) =
        apiService.getDetailStory(token, id)

    suspend fun uploadStory(token: String, story: StoryUploadRequest): StoryUploadResponse {
        val descStory = story.description.toRequestBody("text/plain".toMediaType())
        val reqImage = story.image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            story.image.name,
            reqImage
        )

        val latitude: RequestBody? = story.location?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
        val longitude: RequestBody? = story.location?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())

        return apiService.uploadStory(token, imageMultipart, descStory, latitude, longitude)
    }
}