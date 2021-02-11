package com.github.dromanenko.post

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Post::class], version = 1)
abstract class PostDB : RoomDatabase() {
    abstract fun postDao(): PostDao
}