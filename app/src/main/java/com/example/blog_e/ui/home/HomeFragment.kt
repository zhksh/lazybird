package com.example.blog_e.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.adapters.PostAdapter
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.databinding.FragmentHomeBinding
import com.example.blog_e.utils.PostComparator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var postViewList: ArrayList<PostAPIModel>
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter

    private var isLoading = false
    private var isUserFeed = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = PostAdapter(PostComparator())

        recyclerView = binding.postsListRecyclerView
        recyclerView.apply {
            layoutManager = LinearLayoutManager(root.context)
            adapter = adapter
            setHasFixedSize(true)
        }

        setUpFragmentBinding()

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.getPosts().observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it).also {
                    // Sehr fragwürdig: Der Adapter updated sich nicht automatisch.
                    recyclerView.adapter = adapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.homeState.collect {
                    onToggleClick(it)
                }
            }
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
            adapter.refresh()
            homeViewModel.refreshPosts(false)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun onToggleClick(state: HomeState) {
        binding.toggleButton.isChecked = state.isNotUserFeed
    }

}