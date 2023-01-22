package com.example.blog_e.ui.signUp

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
import com.example.blog_e.data.model.NewUserAPIModel
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val viewModel: SignUpViewModel by viewModels()
        val userViewModel: UserViewModel by activityViewModels()

        val binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        binding.btnAccount.setOnClickListener{
            findNavController().navigate(R.id.login_fragment)
        }

        binding.btnSignUp.setOnClickListener{
            // TODO: Validate input here?
            val username = binding.etUserName.editText?.text.toString()
            val displayName = binding.etDisplayName.editText?.text.toString()
            val password = binding.etPassword.editText?.text.toString()

            val payload = NewUserAPIModel(
                displayName = displayName,
                password = password,
                username = username,
                iconId = ProfilePicture.PICTURE_00.toString(),
            )

            userViewModel.signUp(payload).observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    findNavController().navigate(R.id.action_sign_up_finished)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.uiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { uiState -> handleStateChange(uiState, binding) }
            }
        }

        return binding.root
    }

    private fun handleStateChange(uiState: SignUpState, binding: FragmentSignUpBinding) {
        if (uiState.errorMessage != null) {
            Snackbar.make(binding.root, "${uiState.errorMessage}", Toast.LENGTH_SHORT)
                .show()
        }
    }
}