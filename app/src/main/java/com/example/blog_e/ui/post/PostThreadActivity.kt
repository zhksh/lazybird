package com.example.blog_e.ui.post

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blog_e.adapters.CommentsViewAdapter
import com.example.blog_e.databinding.ActivityPostThreadBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostThreadActivity : AppCompatActivity() {

    private val adapter = CommentsViewAdapter(comments = listOf<Comment>(Comment(id = "Foo", UserInfo(icon_id = "1", username = "username", display_name = "displayName")), Comment(id = "Bar", UserInfo(icon_id = "1", username = "username", display_name = "displayName"))))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: PostThreadViewModel by viewModels()

        val binding = ActivityPostThreadBinding.inflate(layoutInflater)
        binding.commentListRecyclerView.adapter = adapter
        binding.commentListRecyclerView.layoutManager = LinearLayoutManager(this)

        val postId = intent.getStringExtra("POST_ID")
        if (postId != null) {
            val postData = viewModel.getPost(postId)
            postData.observe(this, Observer { post ->
                updateView(post, binding)
            })
        } // TODO: Error on else

        binding.viewModel = viewModel
        setContentView(binding.root)

        // TODO: Can this be moved to xml?
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }

    private fun updateView(post: FullPost, binding: ActivityPostThreadBinding) {
        binding.content.text = post.content
        binding.username.text = "@${post.user.username}"

        if (post.user.display_name == null) {
            binding.displayName.text = post.user.username
        } else {
            binding.displayName.text = post.user.display_name
        }
        binding.likeNumber.text = post.likes.toString()

        // binding.commentListRecyclerView.adapter = CommentsViewAdapter(comments)
        // TODO: Add timestamp
    }
}