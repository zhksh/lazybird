package com.example.blog_e.ui.signUp


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blog_e.R
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.data.model.User
import com.example.blog_e.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupFragmentBinding()

        return root
    }

    private fun setupFragmentBinding() {
        val editTexts = listOf(binding.etUserName, binding.etPassword)
        for (editText in editTexts) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val et1 = binding.etUserName.text.toString().trim()
                    val et2 = binding.etPassword.text.toString().trim()
                    binding.btnSignUp.isEnabled = et1.isNotEmpty() && et2.isNotEmpty()
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
        binding.btnSignUp.setOnClickListener { signUp() }
    }

    private fun signUp() {

        val newUser = User(
            username = binding.etUserName.text.toString(),
            password = binding.btnSignUp.text.toString(),
            profilePicture = ProfilePicture.PICTURE_01
        )

        /* TODO: 1. some how wait for this async task to get a response/watch for a non-null value/response etc.
                 2. create a loading spinner ()
                 both tasks either from here or the viewmodel
        */
        signUpViewModel.signUp(newUser)

        findNavController().navigate(R.id.action_sign_up_fragment_to_navigation_home)

        Snackbar.make(
            binding.root,
            "Welcome, ${newUser.username}!",
            Snackbar.LENGTH_LONG
        ).show()
    }

}