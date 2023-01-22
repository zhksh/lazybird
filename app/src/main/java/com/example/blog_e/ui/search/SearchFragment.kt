package com.example.blog_e.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.blog_e.Config
import com.example.blog_e.data.model.iconIdToProfilePicture
import com.example.blog_e.databinding.FragmentSearchBinding
import com.example.blog_e.databinding.UserSearchResultBinding
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val searchViewModel: SearchViewModel by viewModels()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var _userResult: UserSearchResultBinding? = null
    private val userResult get() = _userResult!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        _userResult = binding.userResult
        val root: View = binding.root

        setupBinding()

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.searchUIState.collect {
                binding.searchView.setQuery(it.query, false)
                binding.loadingSpinner.isVisible = it.isSearching
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.resultUIState.collect { resultState ->
                if (!resultState.isSuccessful) {
                    userResult.blogPost.visibility = View.GONE
                } else {
                    userResult.blogPost.visibility = View.VISIBLE
                    bindUserResultToView(resultState)
                    if (resultState.isFollowing) {
                        userResult.followBtn.text = "Unfollow"
                        userResult.followCheck.isVisible = true
                    } else {
                        userResult.followBtn.text = "Follow"
                        userResult.followCheck.isVisible = false
                    }
                }

                binding.errorMessage.isVisible = resultState.isUserNotFound
            }
        }

        return root
    }

    private fun setupBinding() {
        val followBtn: MaterialButton = userResult.followBtn
        followBtn.setOnClickListener {
            userResult.loadingSpinner.isVisible = true
            userResult.followBtn.isVisible = false
            if (searchViewModel.resultUIState.value.isFollowing) {
                searchViewModel.unFollowUser()
            } else {
                searchViewModel.followUser()
            }
            userResult.followBtn.isVisible = true
            userResult.loadingSpinner.isVisible = false

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
                    searchViewModel.clearLastResult()
                    return false
                }
            }
        )

    }

    private fun performSearch(query: String) {

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.searchUser()
        }
    }

    private fun bindUserResultToView(resultUIState: ResultUIState) {
        val getUser = resultUIState.userAPIModel!!
        userResult.displayName.text = getUser.displayName
        val username = "@${getUser.username}"
        userResult.username.text = username
        userResult.profilePictureView.setImageResource(iconIdToProfilePicture(getUser.iconId).res)
        if (resultUIState.isFollowing) {
            userResult.followBtn.text = "Unfollow"
        } else {
            userResult.followBtn.text = "Follow"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _userResult = null;
        _binding = null
    }

    companion object {
        private val TAG = Config.tag(this.toString())
    }
}