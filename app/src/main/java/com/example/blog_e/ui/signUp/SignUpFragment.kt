package com.example.blog_e.ui.signUp


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blog_e.R
import com.example.blog_e.data.model.NewUserAPIModel
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val signUpViewModel: SignUpViewModel by viewModels()

    private lateinit var username: EditText
    private lateinit var displayName: EditText
    private lateinit var password: EditText

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
        username = binding.etUserName.editText!!
        displayName = binding.etDisplayName.editText!!
        password = binding.etPassword.editText!!

        username.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val et1 = username.text.toString().trim()
                val et2 = password.text.toString().trim()
                binding.btnSignUp.isEnabled = et1.isNotEmpty() && et2.isNotEmpty()

                // TODO: add listener for spaces + make sth like a TextView to notify that there may not be spaces in the username
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val et1 = username.text.toString().trim()
                val et2 = password.text.toString().trim()
                binding.btnSignUp.isEnabled = et1.isNotEmpty() && et2.isNotEmpty()
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.btnSignUp.setOnClickListener { signUp() }
        binding.btnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_sign_up_fragment_to_login_fragment)
        }
    }

    private fun signUp() {

        var defaultDisplayName = username.text.toString()
        if (displayName.text.toString().isNotEmpty()) {
            defaultDisplayName = displayName.text.toString()
        }
        val newUser = NewUserAPIModel(
            username = binding.etUserName.editText!!.text.toString(),
            displayName = defaultDisplayName,
            password = binding.btnSignUp.text.toString(),
            iconId = ProfilePicture.PICTURE_01.toString()
        )

        //TODO: create a loading spinner

        lifecycleScope.launch {
            val isValidUser = signUpViewModel.signUp(newUser)
            if (isValidUser) {
                findNavController().navigate(R.id.action_sign_up_fragment_to_navigation_home)
                Snackbar.make(
                    binding.root,
                    "Welcome, ${newUser.username}!",
                    Snackbar.LENGTH_LONG
                ).show()

            } else {
                Snackbar.make(binding.root, "User already exists", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}