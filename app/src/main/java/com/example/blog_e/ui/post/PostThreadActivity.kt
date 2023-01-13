package com.example.blog_e.ui.post

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.blog_e.R
import com.example.blog_e.databinding.ActivityPostThreadBinding
import com.example.blog_e.databinding.FragmentPostThreadBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostThreadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: PostThreadViewModel by viewModels()

        val postId = intent.getStringExtra("POST_ID")
        viewModel.postId.set(postId)

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