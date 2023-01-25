package com.example.blog_e.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blog_e.Config
import com.example.blog_e.adapters.SearchResultsAdapter
import com.example.blog_e.data.model.UserAPIModel
import com.example.blog_e.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val viewModel: SearchViewModel by viewModels()
        val binding = FragmentSearchBinding.inflate(inflater)

        val adapter = SearchResultsAdapter(emptyList())
        val searchResultRecyclerView = binding.usersRecyclerView
        searchResultRecyclerView.adapter = adapter
        searchResultRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)

        viewModel.searchResult().observe(viewLifecycleOwner) { users ->
            if (users != null) {
                adapter.users = users
                adapter.notifyDataSetChanged()
            }
        }

        binding.searchView.setOnQueryTextListener(
            object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        viewModel.findUsers(query)
                    }
                    return false
                }

                override fun onQueryTextChange(newQuery: String?): Boolean {
                    if (newQuery != null && newQuery.length > 2) {
                        viewModel.findUsers(newQuery)
                    } else {
                        viewModel.resetQuery()
                    }
                    return false
                }
            }
        )

        return binding.root
    }
}