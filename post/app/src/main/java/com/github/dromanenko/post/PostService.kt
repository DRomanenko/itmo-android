package com.github.dromanenko.post

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface PostService {
    @GET("posts")
    fun downloadAllPosts(): Call<List<Post>>

    @DELETE("posts/{num}")
    fun deletePost(@Path("num") num: Int): Call<ResponseBody>

    @POST("posts")
    fun uploadNewPost(@Body newObject: Post): Call<Post>
}