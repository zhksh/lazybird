package com.example.blog_e.ui.VisitProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        Log.i(TAG, "Übergebene Username: ${currentUser}")


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                visitUserModel.fetchUser(currentUser)
                Log.i(TAG, "User Profil geladen")
                visitUserModel.initPager(currentUser)
                Log.i(TAG, "Pager erstell")
            }
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


        postAdapter = PostAdapter(
            PostComparator(),
            root.context
        ) { name -> navigateToVisitProfileFragment(name) }
        recyclerView = binding.postsListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.adapter = postAdapter
        recyclerView.setHasFixedSize(true)

        visitUserModel.posts.observe(viewLifecycleOwner) {
            postAdapter.submitData(lifecycle, it)
        }

        return root

    }

    private fun navigateToVisitProfileFragment(username: String) {

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
