package com.github.dromanenko.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PostViewAdapter(
    var posts: MutableList<Post>
) : RecyclerView.Adapter<PostViewHolder>() {

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) = holder.bind(posts[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : PostViewHolder {
        return PostViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
    }
}
