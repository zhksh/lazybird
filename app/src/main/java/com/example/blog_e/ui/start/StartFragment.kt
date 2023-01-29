package com.example.blog_e.ui.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.blog_e.R
import com.example.blog_e.UserViewModel
import com.example.blog_e.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val userViewModel: UserViewModel by activityViewModels()

        binding.btnLogin.setOnClickListener { login() }
        binding.btnSignUp.setOnClickListener { signUp() }

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            // TODO: Add loading spinner until call finished?
            if (user != null) {
                findNavController().navigate(R.id.action_user_set)
            }
        }

        return root
    }

    private fun signUp() {
        findNavController().navigate(R.id.sign_up_fragment)
    }

    private fun login() {
        findNavController().navigate(R.id.login_fragment)
    }

}