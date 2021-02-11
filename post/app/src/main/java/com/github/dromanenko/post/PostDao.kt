package com.github.dromanenko.post

import androidx.room.*

@Dao
interface PostDao {
    @Query("SELECT * FROM post")
    fun getAll(): List<Post>

    @Insert
    fun insertPost(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPosts(posts: List<Post>)

    @Delete
    fun deletePost(post: Post)

    @Query("DELETE FROM post")
    fun deleteAllPosts()
}