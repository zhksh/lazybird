package com.example.blog_e.ui.VisitProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.Config
import com.example.blog_e.adapters.PostAdapter
import com.example.blog_e.data.model.iconIdToProfilePicture
import com.example.blog_e.databinding.FragmentVisitProfileBinding
import com.example.blog_e.utils.PostComparator
import com.example.blog_e.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VisitProfileFragment : Fragment() {
    private var _binding: FragmentVisitProfileBinding? = null
    private val TAG = Config.tag(this.toString())

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val visitUserModel: ProfileVisitViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.v(TAG, Utils.formatBackstack(findNavController()))

        _binding = FragmentVisitProfileBinding.inflate(layoutInflater)
        val root: View = binding.root

        val currentUser = requireArguments().getString("username")!!
        Log.i(TAG, "Übergebener Username: ${currentUser}")

        postAdapter = PostAdapter(
            PostComparator(),
            root.context
        ) {
            // pass no function for on click events
        }
        recyclerView = binding.postsListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = postAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                visitUserModel.fetchUser(currentUser)
            }
        }

        visitUserModel.getPosts(currentUser).observe(viewLifecycleOwner) {
            postAdapter.submitData(lifecycle, it)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            visitUserModel.uiState.collect { uiState ->
                Log.i(TAG, "Aktueller UI state: " + uiState)
                uiState.user?.let { user ->
                    val username = "@${user.username}"
                    binding.username.text = username
                    binding.nickname.text = user.displayName
                    binding.profilePictureView.setImageResource(iconIdToProfilePicture(user.iconId).res)
                    val postsTitle = "${user.displayName}' posts"
                    binding.postsTitle.text = postsTitle
                }

                if (uiState.isFollowing) {
                    binding.followBtn.text = "Unfollow"
                    binding.followCheck.isVisible = true
                } else {
                    binding.followBtn.text = "Follow"
                    binding.followCheck.isVisible = false
                }
            }
        }


        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            Log.i(TAG, "Es wurde refresht")
        }

        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(username: String) = VisitProfileFragment().apply {
            arguments = Bundle().apply {
                putString("username", username)
            }
        }
    }
}
