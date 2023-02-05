package com.example.blog_e.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.blog_e.data.model.PostAPIModel

class PostComparator : DiffUtil.ItemCallback<PostAPIModel>() {
    override fun areItemsTheSame(oldItem: PostAPIModel, newItem: PostAPIModel): Boolean {
        // Id is unique
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostAPIModel, newItem: PostAPIModel): Boolean {
        return oldItem == newItem
    }

}
