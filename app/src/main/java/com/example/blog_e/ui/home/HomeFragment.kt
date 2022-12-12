package com.example.blog_e.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val postViewList: ArrayList<PostsViewModel> = arrayListOf()

        val post = PostsViewModel(
            profilePicture = 1,
            username = "Max Mustermann"
        )

        val recyclerView: RecyclerView = binding.postsListRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        for (i in 1..20) {
            postViewList.add(post)
        }

        recyclerView.adapter = PostsViewAdapter(postViewList)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}