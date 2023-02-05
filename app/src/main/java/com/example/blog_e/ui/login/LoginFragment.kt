package com.example.blog_e.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blog_e.R
import com.example.blog_e.UserViewModel
import com.example.blog_e.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewModel: LoginViewModel by viewModels()
        val userViewModel: UserViewModel by activityViewModels()

        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        binding.btnAccount.setOnClickListener {
            findNavController().navigate(R.id.sign_up_fragment)
        }

        binding.btnLogin.setOnClickListener {
            login(viewModel, userViewModel, binding)
        }

        binding.etPassword.editText?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login(viewModel, userViewModel, binding)
                val imm =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }

        return binding.root
    }

    private fun login(
        viewModel: LoginViewModel,
        userViewModel: UserViewModel,
        binding: FragmentLoginBinding
    ) {
        viewModel.isLoading.set(true)

        val username = binding.etUserName.editText?.text.toString()
        val password = binding.etPassword.editText?.text.toString()

        val error = validateInput(username, password)
        if (error != "") {
            Snackbar.make(binding.root, error, Toast.LENGTH_SHORT).show()
            viewModel.isLoading.set(false)
            return
        }

        userViewModel.login(username, password).observe(viewLifecycleOwner) { result ->
            viewModel.isLoading.set(false)
            if (result.errorMessage != null) {
                Snackbar.make(binding.root, result.errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(R.id.action_login_finished)
            }
        }
    }

    private fun validateInput(username: String, password: String): String {
        if (username.isEmpty()) {
            return "Please enter a username"
        }

        if (password.isEmpty()) {
            return "Please enter a password"
        }

        return ""
    }
}