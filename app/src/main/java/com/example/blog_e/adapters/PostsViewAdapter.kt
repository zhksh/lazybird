package com.example.blog_e.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.model.ProfilePicture

class PostsViewAdapter(private val postList: List<PostAPIModel>) :
    RecyclerView.Adapter<PostsViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.posts_list_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val postsViewModel = postList[position]

        // holder.profilePictureView.setImageResource(postsViewModel.profilePicture)

        holder.content.text = postsViewModel.content

        holder.username.text = postsViewModel.user.username

        // TODO: mit anderem Model den Display-Namen setzen
        holder.displayName.text = postsViewModel.user.displayName

        postsViewModel.user.iconId
        val imageResourceId = ProfilePicture.PICTURE_01

        holder.profilePictureView.setImageResource(imageResourceId.res)

        holder.likes.text = postsViewModel.likes.toString()
        holder.comments.text = postsViewModel.commentCount.toString()

        // TODO: should be a Data -> get difference with localdate time (now)
        holder.pastTime.text = postsViewModel.timestamp

    }

    override fun getItemCount(): Int {
        return postList.size
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