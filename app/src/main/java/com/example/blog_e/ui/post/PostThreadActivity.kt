package com.example.blog_e.ui.post

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blog_e.adapters.CommentsViewAdapter
import com.example.blog_e.adapters.PostsViewAdapter
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.databinding.ActivityPostThreadBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostThreadActivity : AppCompatActivity() {

    private val adapter = CommentsViewAdapter(comments = listOf())

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

        // TODO: Is it better to set listener directly in xml?
        binding.sendButton.setOnClickListener {
            val comment = binding.commentEditText.text.toString()
            if (comment != "") {
                binding.commentEditText.text.clear()
                hideKeyboard()

                lifecycleScope.launch {
                    viewModel.addComment(comment)
                }
            }
        }

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

        // TODO: Only add diff?
        adapter.comments = post.comments
        adapter.notifyDataSetChanged()

        // binding.commentListRecyclerView.adapter = CommentsViewAdapter(comments)
        // TODO: Add timestamp
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}