package com.example.blog_e.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.model.ProfilePicture
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PostAdapter(differCallback: DiffUtil.ItemCallback<PostAPIModel>) :
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

            it.user.iconId
            val imageResourceId = ProfilePicture.PICTURE_01
            holder.profilePictureView.setImageResource(
                ProfilePicture.values().toList().shuffled().first().res
            )

            holder.likes.text = it.likes.toString()

            holder.comments.text = it.commentCount.toString()

            holder.pastTime.text = calculatePastTime(it.timestamp)
        }
    }

    private fun calculatePastTime(date: String): String {

        println(date)

        val current = LocalDateTime.now(ZoneId.of("Europe/Berlin"))
        var formatter = DateTimeFormatter.ofPattern(PostsViewAdapter.pattern)
        val creationDate: LocalDateTime = LocalDateTime.parse(date, formatter)
        val duration = Duration.between(creationDate, current)

        println(creationDate)
        println(current)
        println(duration.toMinutes())
        if (duration.toDays() > 9) {
            formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            return creationDate.format(formatter)
        }
        if (duration.toDays() > 0) {
            return "${duration.toDays()} d"
        }
        if (duration.toHours() > 0) {
            return "${duration.toHours()} H"
        }
        if (duration.toMinutes() > 0) {
            return "${duration.toMinutes()} M"
        }

        if (duration.toSeconds() > 0) {
            return "${duration.toSeconds()} s"
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

    }



}