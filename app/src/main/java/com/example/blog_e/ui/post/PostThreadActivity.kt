package com.example.blog_e.ui.post

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blog_e.Config
import com.example.blog_e.adapters.CommentsViewAdapter
import com.example.blog_e.databinding.ActivityPostThreadBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostThreadActivity : AppCompatActivity() {

    private val tag = Config.tag(this.toString())
    private val adapter = CommentsViewAdapter(comments = listOf())
    private val viewModel: PostThreadViewModel by viewModels()
    private var postId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        val binding = ActivityPostThreadBinding.inflate(layoutInflater)
        binding.commentListRecyclerView.adapter = adapter
        binding.commentListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.viewModel = viewModel
        setContentView(binding.root)

        postId = intent.getStringExtra("POST_ID")

        if (postId == null) {
            Log.e(tag,"failed to retrieve postId")
            return
        }

        val postData = viewModel.postData()
        postData.observe(this, Observer { post ->
            updateView(post, binding)
        })

        binding.sendButton.setOnClickListener {
            val comment = binding.commentEditText.text.toString()
            if (comment != "") {
                binding.commentEditText.text.clear()
                hideKeyboard()

                lifecycleScope.launch {
                    viewModel.addComment(comment, postId!!)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (postId != null) {
            viewModel.openPostConnection(postId!!)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.closePostConnection()
    }

    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }

    private fun updateView(post: FullPost, binding: ActivityPostThreadBinding) {
        binding.content.text = post.content
        binding.username.text = "@${post.user.username}"
        binding.likeNumber.text = post.likes.toString()

        if (post.user.display_name == null) {
            binding.displayName.text = post.user.username
        } else {
            binding.displayName.text = post.user.display_name
        }

        // TODO: Add icon
        // TODO: Add timestamp
        adapter.comments = post.comments
        adapter.notifyDataSetChanged()
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