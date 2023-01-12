package com.example.blog_e.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.adapters.PostsViewAdapter
import com.example.blog_e.data.model.User
import com.example.blog_e.databinding.FragmentProfileBinding
import com.example.blog_e.models.PostsViewModel
// import com.example.blog_e.ui.home.generatePosts
import com.example.blog_e.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment: Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        /*
        val textView: TextView = binding.textProfile
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        */

        val recyclerView: RecyclerView = binding.postsListRecyclerView
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        // Setup dummy data list; default should be follower list
        val postViewList: ArrayList<PostsViewModel> = arrayListOf()

        /*
        postViewList.addAll(
            generatePosts(
                20,
                "Hello everyone!\nThis is a post from me \uD83D\uDC24\n\nMerry Christmas \uD83C\uDF85",
                R.drawable.baby_yoda_1
            )
        )

        recyclerView.adapter = PostsViewAdapter(postViewList)
    */


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}