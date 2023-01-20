package com.example.blog_e.ui.post

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.adapters.CommentsViewAdapter
import com.example.blog_e.data.model.CommentAPIModel
import com.example.blog_e.data.model.iconToResourceId
import com.example.blog_e.databinding.ActivityPostThreadBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostThreadActivity : AppCompatActivity() {

    private val tag = Config.tag(this.toString())
    private val adapter = CommentsViewAdapter()
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateView(it, binding)
                }
            }
        }

        binding.likeButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.onClickLike()
            }
        }

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

    @SuppressLint("SetTextI18n")
    private fun updateView(post: PostState, binding: ActivityPostThreadBinding) {
        binding.content.text = post.content
        binding.username.text = "@${post.user.username}"
        binding.likeNumber.text = post.likes.toString()

        binding.displayName.text = post.user.displayName ?: post.user.username

        val likeImageResource = if (post.isLiked) {
            R.drawable.heart_filled
        } else {
            R.drawable.heart_outline
        }
        binding.likeButton.setImageResource(likeImageResource)

        binding.profilePictureView.setImageResource(iconToResourceId(post.user.iconId))
        binding.postPastTime.text = post.timeSinceString

        updateCommentAdapter(post.comments)
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Adds all comments not currently present in the comment adapter and notifies the adapter over the inserted items.
     */
    private fun updateCommentAdapter(comments: List<CommentAPIModel>) {
        val newComments = comments.toSet().minus(adapter.comments.toSet())
        val startIndex = adapter.comments.size
        adapter.comments.addAll(newComments)
        adapter.notifyItemRangeInserted(startIndex, newComments.size)
    }
}