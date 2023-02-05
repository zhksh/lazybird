package com.example.blog_e.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.data.model.CommentAPIModel
import com.example.blog_e.data.model.iconIdToProfilePicture

class CommentsViewAdapter(val comments: MutableList<CommentAPIModel> = mutableListOf()) :
    RecyclerView.Adapter<CommentsViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView
        val displayName: TextView
        val profilePicture: ImageView

        init {
            content = view.findViewById(R.id.content)
            displayName = view.findViewById(R.id.displayName)
            profilePicture = view.findViewById(R.id.profilePictureView)
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
        viewHolder.displayName.text = comment.user.displayName ?: comment.user.username
        viewHolder.profilePicture.setImageResource(iconIdToProfilePicture(comment.user.iconId).res)
    }

    override fun getItemCount() = comments.size
}
