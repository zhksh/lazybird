package com.example.blog_e.ui.profile

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.ui.common.PostAdapter
import com.example.blog_e.databinding.FragmentProfileBinding
import com.example.blog_e.ui.common.UserViewModel
import com.example.blog_e.ui.editProfile.EditProfileFragment
import com.example.blog_e.utils.PostComparator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * This class extends the **Fragment** class and is annotated with **@AndroidEntryPoint**.
 * It is a fragment class for the profile screen in the Android app.
 *
 * The main purpose of this class is to display the profile information of the user and to provide
 * functionalities for the user to edit their profile, logout and see their posts.
 */
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
    private lateinit var linearLayoutManager: LinearLayoutManager


    private val startForPostThreadResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            // No need for handling the data; always refresh the adapter when coming back
            //it.data?.let { intent -> }
            postAdapter.refresh()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val root: View = binding.root

        postAdapter = PostAdapter(
            PostComparator(),
            root.context,
            startForPostThreadResult
        ) {
            // pass no function when click on your own profile
        }
        recyclerView = binding.postsListRecyclerView
        linearLayoutManager = LinearLayoutManager(root.context)
        recyclerView.layoutManager = linearLayoutManager
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

        binding.swipeRefresh.setColorSchemeResources(R.color.secondaryColor)
        binding.swipeRefresh.setOnRefreshListener {
            postAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
            userViewModel.renewUserData()
        }

        binding.editButton.setOnClickListener {
            val fragment = EditProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .addToBackStack(null)
                .commit()
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
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
