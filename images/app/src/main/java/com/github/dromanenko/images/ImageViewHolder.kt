package com.github.dromanenko.images

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class ImageViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    fun bind(image: Image) {
        with(root) {
            tv_url.text = image.url
            tv_title.text = image.title
        }
    }
}
