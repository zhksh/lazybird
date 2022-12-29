package com.example.blog_e.ui.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.blog_e.R
import com.example.blog_e.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var sharedViewModel: LoginViewModel

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        // TODO: Use the ViewModel. Decide if you to this in here or in onCreateView
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLogin.setOnClickListener { login() }
        binding.btnAccount.setOnClickListener { toSignUp() }

        return root
    }

    private fun login() {
        // TODO: validate credentials

        val isValidUser = true

        if (isValidUser) {
            // TODO: set auth token

            findNavController().navigate(R.id.action_login_fragment_to_navigation_home)

            Toast.makeText(activity, "Welcome back USERNAME", Toast.LENGTH_SHORT).show()

        } else {
            // TODO: handle wrong input

        }

    }

    private fun toSignUp() {
        findNavController().navigate(R.id.action_login_fragment_to_sign_up_fragment)
    }

}