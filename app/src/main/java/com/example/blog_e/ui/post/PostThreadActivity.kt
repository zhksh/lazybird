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
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.adapters.CommentsViewAdapter
import com.example.blog_e.data.model.CommentAPIModel
import com.example.blog_e.data.model.iconIdToProfilePicture
import com.example.blog_e.databinding.ActivityPostThreadBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * This class extends the **AppCompatActivity** class and is annotated with **@AndroidEntryPoint**.
 *
 * The main purposes of this class are to display the a post thread, including its content, author
 * information, comments and to add new comments to the post thread.
 *
 * Via a web socket, the data is displayed live.
 */
@AndroidEntryPoint
class PostThreadActivity : AppCompatActivity() {

    private val tag = Config.tag(this.toString())
    private val adapter = CommentsViewAdapter()
    private val viewModel: PostThreadViewModel by viewModels()
    private var postId: String? = null

    private lateinit var commentListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)

        val binding = ActivityPostThreadBinding.inflate(layoutInflater)

        commentListRecyclerView = binding.commentListRecyclerView
        commentListRecyclerView.adapter = adapter
        commentListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.viewModel = viewModel
        setContentView(binding.root)

        postId = intent.getStringExtra("POST_ID")

        if (postId == null) {
            Log.e(tag, "failed to retrieve postId")
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

    override fun finish() {
        setResult(RESULT_OK)
        super.finish()
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
            R.drawable.ic_baseline_favorite_24
        } else {
            R.drawable.ic_baseline_favorite_border_24
        }
        binding.likeButton.setImageResource(likeImageResource)

        binding.profilePictureView.setImageResource(iconIdToProfilePicture(post.user.iconId).res)
        binding.postPastTime.text = post.timeSinceString

        updateCommentAdapter(post.comments)
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
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

        // scroll to the last comment
        commentListRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }
}
