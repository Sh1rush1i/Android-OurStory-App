package com.submis.ourstory.data.init.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.submis.ourstory.data.init.entity.RemoteKeys
import com.submis.ourstory.data.init.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}