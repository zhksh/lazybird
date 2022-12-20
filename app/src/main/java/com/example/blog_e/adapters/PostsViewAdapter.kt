package com.example.blog_e.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.models.PostsViewModel

class PostsViewAdapter(private val postList: List<PostsViewModel>) :
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

        holder.username.text = postsViewModel.username

        holder.profilePictureView.setImageResource(postsViewModel.profilePicture)

    }

    override fun getItemCount(): Int {
        return postList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePictureView: ImageView = itemView.findViewById(R.id.profilePictureView)
        val content: TextView = itemView.findViewById(R.id.content)
        val username: TextView = itemView.findViewById(R.id.username)
    }
}