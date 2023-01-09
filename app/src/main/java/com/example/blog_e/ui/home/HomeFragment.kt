package com.example.blog_e.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.adapters.PostsViewAdapter
import com.example.blog_e.data.model.Post
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.databinding.FragmentHomeBinding
import com.example.blog_e.models.PostsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.postsListRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        setUpFragmentBinding()

        // fetch blogs from user feed
        lifecycleScope.launch {
            val postViewList: List<PostAPIModel> = homeViewModel.fetchBlogs(true)

            recyclerView.adapter = PostsViewAdapter(postViewList)
        }


//        binding.button22.setOnClickListener {
//            homeViewModel.fetchBlogs(true)
//        }

        return root
    }

    private fun mapPosts(postViewList: List<Post>): List<PostsViewModel> {
        return postViewList.map {
            PostsViewModel(
                profilePicture = R.drawable.baby_yoda_1,
                username = it.author.toString(),
                createdSince = it.publicationDate.toString(),
                content = it.content,
                commentNumber = it.commentCount,
            )
        }
    }

    private fun setUpFragmentBinding() {

        val toggleButton: ToggleButton = binding.toggleButton

        toggleButton.setOnCheckedChangeListener { _, showGlobal ->
            if (showGlobal) {
                lifecycleScope.launch {
                    val postViewList: List<PostAPIModel> = homeViewModel.fetchBlogs(false)

                    recyclerView.adapter = PostsViewAdapter(postViewList)
                }
            } else {
                lifecycleScope.launch {
                    val postViewList: List<PostAPIModel> = homeViewModel.fetchBlogs(true)

                    recyclerView.adapter = PostsViewAdapter(postViewList)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}