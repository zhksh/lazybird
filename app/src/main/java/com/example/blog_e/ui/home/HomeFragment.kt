package com.example.blog_e.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.adapters.PostsViewAdapter
import com.example.blog_e.databinding.FragmentHomeBinding
import com.example.blog_e.models.PostsViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toggleButton: ToggleButton = binding.toggleButton

        val recyclerView: RecyclerView = binding.postsListRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(root.context)

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// TODO: replace this function with service calls for fetching the posts
fun generatePosts(number: Int, content: String): List<PostsViewModel> {
    val posts: ArrayList<PostsViewModel> = arrayListOf()
    for (i in 0..number) {
        posts.add(
            PostsViewModel(
                profilePicture = 1,
                username = "Max Mustermann",
                content = content
            )
        )
    }
    return posts
}