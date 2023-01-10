package com.example.blog_e.ui.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.blog_e.databinding.FragmentSignUpBinding
import com.example.blog_e.ui.login.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewModel: SignUpViewModel by viewModels()

        val binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.uiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect{ uiState -> handleStateChange(uiState, binding)}
            }
        }

        return binding.root
    }

    private fun handleStateChange(uiState: SignUpState, binding: FragmentSignUpBinding) {
        if (uiState.isUserLoggedIn) {
            // TODO: navigate
        }

        if (uiState.errorMessage != null) {
            // TODO: Display error message
        }
    }
}