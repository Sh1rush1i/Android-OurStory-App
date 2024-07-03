package com.submis.ourstory.utils

import androidx.test.espresso.idling.CountingIdlingResource

object IdlingResourceHelper {

    private const val RESOURCE_NAME = "GLOBAL_RESOURCE"

    @JvmField
    val idlingResource = CountingIdlingResource(RESOURCE_NAME)

    fun increment() {
        idlingResource.increment()
    }

    fun decrement() {
        if (!idlingResource.isIdleNow) {
            idlingResource.decrement()
        }
    }
}

inline fun <T> withIdlingResource(function: () -> T): T {
    IdlingResourceHelper.increment()
    return try {
        function()
    } finally {
        IdlingResourceHelper.decrement()
    }
}