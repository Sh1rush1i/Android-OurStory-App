package com.submis.ourstory.dom.story.model

import java.io.File
import android.location.Location

data class StoryUploadRequest(
    val image: File,
    val description: String,
    val location: Location? = null
)
