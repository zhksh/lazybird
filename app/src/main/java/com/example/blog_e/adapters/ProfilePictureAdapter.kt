package com.example.blog_e.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.data.model.ProfilePicture

class ProfilePictureAdapter(
    private val images: MutableList<ProfilePicture>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<ProfilePictureAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profilePicture = images[position]
        holder.imageResource.setImageResource(profilePicture.res)
        holder.imageResource.setOnClickListener {
            listener.onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        return images.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageResource: ImageView = view.findViewById(R.id.dialog_imageview)

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}
