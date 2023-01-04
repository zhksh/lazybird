package com.example.blog_e.ui.signUp


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.blog_e.R
import com.example.blog_e.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println("Created")
        signUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnSignUp.setOnClickListener { signUp() }

        return root
    }

    private fun signUp() {
        // TODO: Execute sign up

        signUpViewModel.signUp()

        val signUpResponseIsSuccessful = true

        if (signUpResponseIsSuccessful) {
            // TODO: set auth token

            findNavController().navigate(R.id.action_sign_up_fragment_to_navigation_home)

            Toast.makeText(
                activity,
                "Successfully created you account, USERNAME",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // TODO: handle unsuccessful sign up
        }
    }
}