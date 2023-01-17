package com.example.blog_e.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.Config
import com.example.blog_e.adapters.PostAdapter
import com.example.blog_e.databinding.FragmentHomeBinding
import com.example.blog_e.utils.PostComparator
import com.example.blog_e.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val TAG = Config.tag(this.toString())
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, Utils.formatBackstack(findNavController()))

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        postAdapter = PostAdapter(PostComparator())
        linearLayoutManager = LinearLayoutManager(root.context)

        recyclerView = binding.postsListRecyclerView
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = postAdapter
        recyclerView.setHasFixedSize(true)

        setUpFragmentBinding()

        homeViewModel.posts.observe(viewLifecycleOwner) {
            postAdapter.submitData(lifecycle, it)
        }

        homeViewModel.isUserFeed.observe(viewLifecycleOwner) {
            onToggleClick(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setUpFragmentBinding() {
        binding.toggleButton.setOnCheckedChangeListener { _, showGlobal ->
            homeViewModel.onClickUserFeed(showGlobal)
            recyclerView.smoothScrollToPosition(0)
        }

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.refreshPosts(true)
            //TODO: Den Adapter zu refreshen sorgt aktuell dafür, dass hier das Paging nicht richtig funktioniert. Nochmal genauer naschschauen!!!
//            postAdapter.refresh()
            homeViewModel.refreshPosts(false)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun onToggleClick(isGlobal: Boolean) {
        binding.toggleButton.isChecked = !isGlobal
    }

}