package com.submis.ourstory.data.init.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(

    @field:SerializedName("listStory")
	val listStory: List<StoryItem>,

    @field:SerializedName("error")
	val error: Boolean? = null,

    @field:SerializedName("message")
	val message: String? = null
)

data class StoryItem(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("lat")
	val lat: Double? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,
)
