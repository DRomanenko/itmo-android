package com.github.dromanenko.post

import android.app.Application
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.moshi.MoshiConverterFactory

class Retrofit : Application() {
    companion object {
        lateinit var instance: Retrofit
        private const val URL = "https://jsonplaceholder.typicode.com/"
        private const val DATABASE = "local_posts.db"
    }

    var postViewAdapter: PostViewAdapter? = null
    lateinit var postDao: PostDao
    lateinit var retrofit: retrofit2.Retrofit
    lateinit var postService: PostService

    override fun onCreate() {
        super.onCreate()
        instance = this

        retrofit = retrofit2.Retrofit
            .Builder()
            .baseUrl(URL)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(
                        KotlinJsonAdapterFactory()
                    ).build()
                )
            )
            .build()
        postService = retrofit.create(PostService::class.java)
        postDao = Room.databaseBuilder(
            applicationContext,
            PostDB::class.java, DATABASE
        ).build().postDao()
    }

    fun insertNewPost(post: Post, success: (post: Post) -> Unit, failure: (Throwable) -> Unit) {
        postService.uploadNewPost(post).enqueue(callback(failure, success))
        GlobalScope.launch {
            postDao.insertPost(post)
        }
    }

    fun insertAllPostsDB(posts: List<Post>) {
        GlobalScope.launch {
            postDao.insertAllPosts(posts)
        }
    }

    fun loadPosts() {
        GlobalScope.launch {
            postViewAdapter?.apply {
                posts = postDao.getAll() as MutableList<Post>
                notifyDataSetChanged()
            }
        }
    }

    fun update(success: (response: List<Post>) -> Unit, failure: (Throwable) -> Unit) {
        postService.downloadAllPosts().enqueue(callback(failure, success))
    }

    fun deletePost(id: Int) {
        postService.deletePost(id).enqueue(callback({}, {}))
    }

    fun deletePostDB(post: Post) {
        GlobalScope.launch {
            postDao.deletePost(post)
        }
    }

    private fun <T> callback(
        failure: (Throwable) -> Unit,
        success: (response: T) -> Unit
    ): Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                failure(t)
                call.cancel()
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    success(response.body()!!)
                }
                call.cancel()
            }
        }
    }

    fun clearDB() {
        GlobalScope.launch {
            postDao.deleteAllPosts()
        }
    }
}