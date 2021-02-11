package com.github.dromanenko.post

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class PostViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
    fun bind(post: Post) {
        root.bn_delete.setOnClickListener {
            Retrofit.instance.deletePost(post.id)
            var deleteId = -1
            Retrofit.instance.postViewAdapter?.posts?.forEachIndexed { index, curPost ->
                if (curPost.id == post.id) {
                    deleteId = index
                    Retrofit.instance.deletePostDB(curPost)
                    return@forEachIndexed
                }
            }
            Retrofit.instance.postViewAdapter?.posts?.removeAt(deleteId)
            Retrofit.instance.postViewAdapter?.notifyDataSetChanged()
        }
        with(root) {
            tv_title.text = post.title
            tv_context.text = post.body
        }
    }
}
