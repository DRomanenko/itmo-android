package com.github.dromanenko.post

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.new_post.*

class NewPost : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_post)
        bn.setOnClickListener {
            val title = et_title.text.toString()
            val text = et_context.text.toString()
            var lastId = -1
            Retrofit.instance.postViewAdapter?.posts?.forEach { if (it.id > lastId) lastId = it.id }
            Post(++lastId, 38, title, text).apply {
                Retrofit.instance.insertNewPost(
                    this, {
                        Toast.makeText(
                            this@NewPost,
                            "Post #$lastId (title=$title, ...) successfully published by user$userId",
                            Toast.LENGTH_LONG
                        ).show()
                    }, {
                        Toast.makeText(
                            this@NewPost,
                            "Something went wrong when sending a request, but the post has been saved",
                            Toast.LENGTH_LONG
                        ).show()
                    })
                Retrofit.instance.postViewAdapter?.posts?.add(this)
                Retrofit.instance.postViewAdapter?.notifyDataSetChanged()
                finish()
            }
        }
    }
}