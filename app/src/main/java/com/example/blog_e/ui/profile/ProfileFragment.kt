package com.example.blog_e.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.adapters.PostsViewAdapter
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.data.model.User
import com.example.blog_e.databinding.FragmentProfileBinding
import com.example.blog_e.models.PostsViewModel
// import com.example.blog_e.ui.home.generatePosts
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment: Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = profileViewModel

        val root: View = binding.root

        profileViewModel.getProfile().observe(viewLifecycleOwner, Observer<User?>{ user ->
            println("observer called")
            if (user == null) {
                findNavController().navigate(R.id.start_fragment)
            } else {
                // TODO: Update UI with user
            }
        })

        recyclerView = binding.postsListRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        lifecycleScope.launch {
            // TODO: We might want to consider moving the data fetching to view model and only subscribe here through LiveData or any other observer
            // TODO: See https://uni2work.ifi.lmu.de/course/W22/IfI/MSP/file/Vorlesung%205:%20Modern%20Android%20Architecture/show
            val postViewList: List<PostAPIModel> = profileViewModel.fetchBlogs()
            recyclerView.adapter = PostsViewAdapter(postViewList)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}