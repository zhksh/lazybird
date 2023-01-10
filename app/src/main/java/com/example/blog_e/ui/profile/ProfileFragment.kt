package com.example.blog_e.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.adapters.PostsViewAdapter
import com.example.blog_e.data.model.PostAPIModel
import com.example.blog_e.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

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
        val root: View = binding.root

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
        val postViewList: ArrayList<PostAPIModel> = arrayListOf()

//    TODO hier was richtiges laden

        recyclerView.adapter = PostsViewAdapter(postViewList)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}