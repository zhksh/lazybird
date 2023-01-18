package com.example.blog_e.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.blog_e.Config
import com.example.blog_e.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val searchViewModel: SearchViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val TAG = Config.tag(this.toString())
        //init the view
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.searchUIState.collect {
                binding.searchView.setQuery(it.query, it.isSearching)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.resultUIState.collect {
            }
        }


        binding.searchView.setOnQueryTextListener(
            object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.i(TAG, "onQueryTextSubmit: $query")
                    query?.let { performSearch(it) }
                    return false
                }

                override fun onQueryTextChange(newQuery: String?): Boolean {
                    searchViewModel.updateQuery(newQuery.toString())
                    return false
                }
            }
        )

        return root
    }

    private fun performSearch(query: String) {

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.searchUser()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}