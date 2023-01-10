package com.example.blog_e.ui.login

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
import com.example.blog_e.data.model.LoginPayload
import com.example.blog_e.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var username: EditText
    private lateinit var password: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupFragmentBinding()

        return root
    }

    private fun setupFragmentBinding() {
        username = binding.etUserName.editText!!
        password = binding.etPassword.editText!!
        username.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val et1 = username.text.toString().trim()
                val et2 = password.text.toString().trim()
                binding.btnLogin.isEnabled = et1.isNotEmpty() && et2.isNotEmpty()

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
                binding.btnLogin.isEnabled = et1.isNotEmpty() && et2.isNotEmpty()
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.btnLogin.setOnClickListener { login() }
        binding.btnAccount.setOnClickListener { toSignUp() }

    }

    private fun login() {

        val loginUser = LoginPayload(
            username.text.toString(),
            password.text.toString()
        )


        lifecycleScope.launch {
            val isValidUser = loginViewModel.login(loginUser)
            if (isValidUser) {
                findNavController().navigate(R.id.action_login_fragment_to_navigation_home)
                Snackbar.make(
                    binding.root,
                    "Welcome back, ${loginUser.username}",
                    Toast.LENGTH_SHORT
                )
                    .show()

            } else {
                Snackbar.make(binding.root, "Invalid username or password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun toSignUp() {
        findNavController().navigate(R.id.action_login_fragment_to_sign_up_fragment)
    }

}