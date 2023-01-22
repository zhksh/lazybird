package com.example.blog_e.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blog_e.R
import com.example.blog_e.UserViewModel
import com.example.blog_e.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.uiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { uiState -> handleStateChange(uiState, binding) }
            }
        }

        binding.btnAccount.setOnClickListener{
            findNavController().navigate(R.id.sign_up_fragment)
        }

        binding.btnLogin.setOnClickListener{
            // TODO: Validate input here?
            val username = binding.etUserName.editText?.text.toString()
            val password = binding.etPassword.editText?.text.toString()

            userViewModel.login(username, password).observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    findNavController().navigate(R.id.action_login_finished)
                }
            }
        }

        return binding.root
    }

    private fun handleStateChange(uiState: LoginState, binding: FragmentLoginBinding) {
        if (uiState.errorMessage != null) {
            Snackbar.make(binding.root, "${uiState.errorMessage}", Toast.LENGTH_SHORT)
                .show()
        }
    }
}