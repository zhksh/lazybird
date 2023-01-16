package com.example.blog_e.ui.post

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.blog_e.databinding.ActivityPostThreadBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostThreadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: PostThreadViewModel by viewModels()

        val postId = intent.getStringExtra("POST_ID")

        if (postId != null) {
            val postData = viewModel.getPost(postId)
            postData.observe(this, Observer { post ->
                viewModel.postId.set(post)
            })
        }

        val binding = ActivityPostThreadBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        setContentView(binding.root)

        // TODO: Can this be moved to xml?
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        this.finish()
        return true
    }
}