package com.submis.ourstory.utils

import com.submis.ourstory.dom.story.model.Story

object DataDummy {
    fun dummyEntityTest(): List<Story> {
        return (0..100).map {
            Story(
                it.toString(),
                "name $it",
                "photo $it",
                "createdAt $it",
                "description $it",
                null,
                null,
            )
        }
    }
}