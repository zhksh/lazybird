package com.example.blog_e.ui.profile

// import com.example.blog_e.ui.home.generatePosts
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.adapters.PostAdapter
import com.example.blog_e.data.model.User
import com.example.blog_e.databinding.FragmentProfileBinding
import com.example.blog_e.utils.PostComparator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = profileViewModel

        val root: View = binding.root

        postAdapter = PostAdapter(PostComparator())
        recyclerView = binding.postsListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.adapter = postAdapter
        recyclerView.setHasFixedSize(true)

        profileViewModel.posts.observe(viewLifecycleOwner) {
            postAdapter.submitData(lifecycle, it)
        }

        profileViewModel.getProfile().observe(viewLifecycleOwner, Observer<User?> { user ->
            println("observer called")
            if (user == null) {
                findNavController().navigate(R.id.start_fragment)
            } else {
                // TODO: Update UI with user

            }
        })



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}