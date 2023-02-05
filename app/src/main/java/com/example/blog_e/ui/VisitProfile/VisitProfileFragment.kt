package com.example.blog_e.ui.VisitProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.adapters.PostAdapter
import com.example.blog_e.data.model.iconIdToProfilePicture
import com.example.blog_e.databinding.FragmentVisitProfileBinding
import com.example.blog_e.utils.PostComparator
import com.example.blog_e.utils.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * This class extends the **Fragment** class and is annotated with **@AndroidEntryPoint**.
 * It is a fragment class for the profile screen in the Android app.
 *
 * The main purpose of this class is to display the profile information of a user that is being
 * visited and to provide functionalities to follow, unfollow and see the posts of this visited user.
 */
@AndroidEntryPoint
class VisitProfileFragment : Fragment() {
    private var _binding: FragmentVisitProfileBinding? = null
    private val TAG = Config.tag(this.toString())

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val visitUserModel: VisitProfileViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var postAdapter: PostAdapter
    private lateinit var actionBar: ActionBar

    private val startForPostThreadResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == android.app.Activity.RESULT_OK) {
            postAdapter.refresh()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.v(TAG, Utils.formatBackstack(findNavController()))

        actionBar = (activity as AppCompatActivity).supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.show()

        _binding = FragmentVisitProfileBinding.inflate(layoutInflater)
        val root: View = binding.root

        val currentUser = requireArguments().getString("username")!!
        actionBar.title = "Profile of $currentUser"
        Log.i(TAG, "Übergebener Username: ${currentUser}")

        postAdapter = PostAdapter(
            PostComparator(),
            root.context,
            startForPostThreadResult
        ) {
            // pass no function for on click events
        }
        recyclerView = binding.postsListRecyclerView
        linearLayoutManager = LinearLayoutManager(root.context)
        recyclerView.layoutManager = linearLayoutManager
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
                    binding.username.text = user.username
                    binding.nickname.text = user.displayName
                    binding.profilePictureView.setImageResource(iconIdToProfilePicture(user.iconId).res)
                    val postsTitle = "${user.displayName ?: user.username}' posts"
                    binding.postsTitle.text = postsTitle
                    binding.selfDescription.text = user.selfDescription
                }

                if (uiState.isFollowing) {
                    binding.followBtn.text = "Unfollow"
                } else {
                    binding.followBtn.text = "Follow"
                }
            }
        }


        binding.swipeRefresh.setColorSchemeResources(R.color.secondaryColor)
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
        }

        binding.followBtn.setOnClickListener {
            binding.loadingSpinner.isVisible = true
            binding.followBtn.isVisible = false
            if (visitUserModel.uiState.value.isFollowing) {
                visitUserModel.unFollowUser()
            } else {
                visitUserModel.followUser()
            }
            binding.followBtn.isVisible = true
            binding.loadingSpinner.isVisible = false

        }

        val fab: FloatingActionButton = binding.fab

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 || linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                    fab.hide()
                } else if (dy < 0) {
                    fab.show()
                }
            }
        })

        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
            fab.hide()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        actionBar.hide()
    }

    companion object {
        fun newInstance(username: String) = VisitProfileFragment().apply {
            arguments = Bundle().apply {
                putString("username", username)
            }
        }
    }
}
