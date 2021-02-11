package com.github.dromanenko.post

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Retrofit.instance.loadPosts()
        if (Retrofit.instance.postViewAdapter == null)
            Retrofit.instance.postViewAdapter = PostViewAdapter(mutableListOf())
        updateRW()

        bn_update.setOnClickListener {
            switchVisible(true)
            Retrofit.instance.postViewAdapter = PostViewAdapter(mutableListOf())
            Retrofit.instance.postViewAdapter?.notifyDataSetChanged()
            Retrofit.instance.update({
                Retrofit.instance.clearDB()
                Retrofit.instance.postViewAdapter = PostViewAdapter(it as MutableList<Post>)
                Retrofit.instance.postViewAdapter?.notifyDataSetChanged()
                Retrofit.instance.insertAllPostsDB(it)
                updateRW()
            }, {
                Retrofit.instance.loadPosts()
                updateRW()
            })
        }

        bn_new.setOnClickListener {
            startActivity(Intent(this, NewPost::class.java))
            Retrofit.instance.postViewAdapter?.notifyDataSetChanged()
        }

        switchVisible(false)
    }

    private fun updateRW() {
        val viewManager = LinearLayoutManager(this@MainActivity)
        rw.apply {
            adapter = Retrofit.instance.postViewAdapter
            layoutManager = viewManager
        }
        switchVisible(false)
    }

    private fun switchVisible(isDownloading: Boolean) {
        pb.visibility = if (isDownloading) View.VISIBLE else View.INVISIBLE
        val isHide = if (isDownloading) View.INVISIBLE else View.VISIBLE
        rw.visibility = isHide
        ll.visibility = isHide
    }
}