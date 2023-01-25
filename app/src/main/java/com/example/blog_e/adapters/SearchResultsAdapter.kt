package com.example.blog_e.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.data.model.CommentAPIModel
import com.example.blog_e.data.model.UserAPIModel
import com.example.blog_e.data.model.iconIdToProfilePicture

class SearchResultsAdapter(var users: List<UserAPIModel> = emptyList()) :
    RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.username)
        val displayName: TextView = view.findViewById(R.id.displayName)
        val profilePicture: ImageView = view.findViewById(R.id.profilePictureView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.user_search_result, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val user = users[position]

        viewHolder.username.text = user.username
        viewHolder.profilePicture.setImageResource(iconIdToProfilePicture(user.iconId).res)
        if (user.displayName == null) {
            viewHolder.displayName.text = user.username
        } else {
            viewHolder.displayName.text = user.displayName
        }
    }

    override fun getItemCount() = users.size
}