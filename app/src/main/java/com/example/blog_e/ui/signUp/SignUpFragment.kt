package com.example.blog_e.ui.signUp

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
import com.example.blog_e.data.model.NewUserAPIModel
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.databinding.FragmentSignUpBinding
import com.example.blog_e.utils.validatePassword
import com.example.blog_e.utils.validateUsername
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

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
            viewModel.isLoading.set(true)

            val username = binding.etUserName.editText?.text.toString()
            val displayName = binding.etDisplayName.editText?.text.toString()
            val password = binding.etPassword.editText?.text.toString()

            val error = validateInput(username, password, displayName)
            if (error != "") {
                Snackbar.make(binding.root, error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val payload = NewUserAPIModel(
                displayName = displayName,
                password = password,
                username = username,
                iconId = ProfilePicture.PICTURE_00.toString(),
            )

            userViewModel.signUp(payload).observe(viewLifecycleOwner) { result ->
                viewModel.isLoading.set(false)
                if (result.errorMessage != null) {
                    Snackbar.make(binding.root, result.errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.action_sign_up_finished)
                }
            }
        }

        return binding.root
    }

    private fun validateInput(username: String, password: String, displayName : String): String {
        val err = validateUsername(username)
        if (err != "") {
            return err
        }

        if (displayName == "") {
            return "Please enter a display name"
        }

        return validatePassword(password)
    }
}