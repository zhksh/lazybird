package com.example.blog_e.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.adapters.PostsViewAdapter
import com.example.blog_e.databinding.FragmentHomeBinding
import com.example.blog_e.models.PostsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupFragmentBinding()

        return root
    }

    private fun setupFragmentBinding() {
        val toggleButton: ToggleButton = binding.toggleButton

        val recyclerView: RecyclerView = binding.postsListRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(binding.root.context)

        // Setup dummy data list; default should be follower list
        val postViewList: ArrayList<PostsViewModel> = arrayListOf()

        postViewList.addAll(
            generatePosts(
                20,
                "Hello everyone!\nThis is a post from your followers"
            )
        )

        recyclerView.adapter = PostsViewAdapter(postViewList)

        toggleButton.setOnCheckedChangeListener { _, showGlobal ->
            if (showGlobal) {
                postViewList.clear()
                postViewList.addAll(
                    generatePosts(
                        20,
                        "Hello worlds!\nThis is a post from someone globally\nHurray!\n\n#global"
                    )
                )
                recyclerView.adapter = PostsViewAdapter(postViewList)
            } else {
                postViewList.clear()
                postViewList.addAll(
                    generatePosts(
                        20,
                        "Hello everyone!\nThis is a post from your followers"
                    )
                )
                recyclerView.adapter = PostsViewAdapter(postViewList)
            }
        }

        binding.button22.setOnClickListener {
            homeViewModel.fetchBlogs(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// TODO: replace this function with service calls for fetching the posts
fun generatePosts(number: Int, content: String, img: Int? = null): List<PostsViewModel> {
    val posts: ArrayList<PostsViewModel> = arrayListOf()
    for (i in 0..number) {
        posts.add(
            PostsViewModel(
                profilePicture = img ?: R.drawable.ic_baseline_account_circle_24,
                username = "Max Mustermann",
                content = content
            )
        )
    }
    return posts
}