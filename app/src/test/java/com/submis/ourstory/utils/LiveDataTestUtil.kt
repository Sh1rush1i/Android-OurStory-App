package com.submis.ourstory.utils

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.awaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: (() -> Unit)? = null,
    defaultValue: T? = null,
    timeoutMessage: String = "LiveData value was never set."
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@awaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    try {
        afterObserve?.invoke()
        if (!latch.await(time, timeUnit)) {
            if (defaultValue != null) {
                return defaultValue
            } else {
                throw TimeoutException(timeoutMessage)
            }
        }
    } finally {
        this.removeObserver(observer)
    }

    return data as T
}
