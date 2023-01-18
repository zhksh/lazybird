package com.example.blog_e.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.ui.post.PostThreadActivity
import com.example.blog_e.utils.Utils
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostAdapter(differCallback: DiffUtil.ItemCallback<PostAPIModel>, private val context: Context) :
    PagingDataAdapter<PostAPIModel, PostAdapter.ViewHolder>(differCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.posts_list_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postsViewModel = getItem(position)
        postsViewModel?.let {
            holder.content.text = it.content
            val username = "@" + it.user.username
            holder.username.text = username
            holder.displayName.text = it.user.displayName

            var imageResourceId: Int
            try {
                imageResourceId = ProfilePicture.valueOf(it.user.iconId).res
            }
            catch (e: IllegalArgumentException){
                imageResourceId = ProfilePicture.PICTURE_05.res
            }

            holder.profilePictureView.setImageResource(imageResourceId)
            holder.likes.text = it.likes.toString()
            holder.comments.text = it.commentCount.toString()

            holder.pastTime.text = calculatePastTime(it.timestamp)

            val postId = it.id
            holder.content.setOnClickListener {
                openPost(postId)
            }

            holder.comments.setOnClickListener {
                openPost(postId)
            }

            holder.commentBubble.setOnClickListener {
                openPost(postId)
            }
        }
    }

    private fun calculatePastTime(date: String): String {

        val current = Utils.currentDate()
        var formatter = DateTimeFormatter.ofPattern(Config.dateFormat)
        val creationDate: LocalDateTime = LocalDateTime.parse(date, formatter)
        val duration = Duration.between(creationDate, current)


        if (duration.toDays() > 9) {
            formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            return creationDate.format(formatter)
        }
        if (duration.toDays() > 0) {
            return "${duration.toDays()}d"
        }
        if (duration.toHours() > 0) {
            return "${duration.toHours()}h"
        }
        if (duration.toMinutes() > 0) {
            return "${duration.toMinutes()}m"
        }

        if (duration.seconds > 0) {
            return "${duration.seconds}s"
        }

        return "just posted"

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePictureView: ImageView = itemView.findViewById(R.id.profilePictureView)
        val content: TextView = itemView.findViewById(R.id.content)
        val username: TextView = itemView.findViewById(R.id.username)
        val displayName: TextView = itemView.findViewById(R.id.displayName)
        val likes: TextView = itemView.findViewById(R.id.likeNumber)
        val comments: TextView = itemView.findViewById(R.id.commentNumber)
        val pastTime: TextView = itemView.findViewById(R.id.postPastTime)
        val commentBubble: ImageView = itemView.findViewById(R.id.imageView)
    }

    private fun openPost(postId: String) {
        val intent = Intent(context, PostThreadActivity::class.java)
        intent.putExtra("POST_ID", postId)
        context.startActivity(intent)
    }
}