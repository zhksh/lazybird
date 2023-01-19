package com.example.blog_e.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.data.model.CommentAPIModel

class CommentsViewAdapter(var comments: List<CommentAPIModel>) :
    RecyclerView.Adapter<CommentsViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView
        val displayName: TextView

        init {
            content = view.findViewById(R.id.content)
            displayName = view.findViewById(R.id.displayName)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.comment_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val comment = comments[position]

        viewHolder.content.text = comment.content
        if (comment.user.displayName == null) {
            viewHolder.displayName.text = comments[position].user.username
        } else {
            viewHolder.displayName.text = comments[position].user.displayName
        }

        // TODO: Add icon
    }

    override fun getItemCount() = comments.size
}