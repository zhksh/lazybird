package com.example.blog_e.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.UserViewModel
import com.example.blog_e.adapters.PostAdapter
import com.example.blog_e.databinding.FragmentProfileBinding
import com.example.blog_e.ui.editProfile.EditProfileFragment
import com.example.blog_e.utils.PostComparator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    private val TAG = Config.tag(this.toString())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val root: View = binding.root

        postAdapter = PostAdapter(
            PostComparator(),
            root.context
        ) {
            // pass no function when click on your own profile}#
        }
        recyclerView = binding.postsListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.adapter = postAdapter
        recyclerView.setHasFixedSize(true)

        profileViewModel.posts.observe(viewLifecycleOwner) {
            postAdapter.submitData(lifecycle, it)
        }

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.username.text = user.username
                binding.nickname.text = user.displayName
                binding.selfDescription.text = user.selfDescription
                binding.profilePictureView.setImageResource(user.profilePicture.res)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    profileViewModel.profileUiState.collect {
                        Log.v(TAG, "collecting ui state: ${it.toString()}")
                        if (it.errMsg.isNotBlank())
                            Snackbar.make(binding.root, it.errMsg, Toast.LENGTH_SHORT).show()
                    }
                }
                userViewModel.renewUserData()
            }
        }

        binding.logOutButton.setOnClickListener {
            userViewModel.logout()
            findNavController().navigate(R.id.action_logout)
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            userViewModel.renewUserData()
        }

        binding.editProfile.setOnClickListener {
            val fragment = EditProfileFragment()
            parentFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment_activity_main, fragment)
                .addToBackStack(null)
                .commit()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}