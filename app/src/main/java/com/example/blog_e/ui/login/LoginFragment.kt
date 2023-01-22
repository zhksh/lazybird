package com.example.blog_e.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.btnAccount.setOnClickListener{
            findNavController().navigate(R.id.sign_up_fragment)
        }

        binding.btnLogin.setOnClickListener{
            viewModel.isLoading.set(true)

            // TODO: Validate input here?
            val username = binding.etUserName.editText?.text.toString()
            val password = binding.etPassword.editText?.text.toString()

            userViewModel.login(username, password).observe(viewLifecycleOwner) { result ->
                viewModel.isLoading.set(false)
                if (result.errorMessage != null) {
                    Snackbar.make(binding.root, result.errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.action_login_finished)
                }
            }
        }

        return binding.root
    }
}