package com.example.blog_e.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blog_e.R
import com.example.blog_e.data.model.LoginPayload
import com.example.blog_e.databinding.FragmentLoginBinding
import com.example.blog_e.databinding.FragmentSignUpBinding
import com.example.blog_e.ui.signUp.SignUpViewModel
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

        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.uiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect{ uiState -> handleStateChange(uiState, binding)}
            }
        }

        return binding.root
    }

    private fun handleStateChange(uiState: LoginState, binding: FragmentLoginBinding) {
        if (uiState.navTo != null) {
            findNavController().navigate(uiState.navTo)
            return
        }

        if (uiState.errorMessage != null) {
            Snackbar.make(binding.root, "${uiState.errorMessage}", Toast.LENGTH_SHORT)
                .show()
        }
    }
}