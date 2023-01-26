package com.example.blog_e.ui.editProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.blog_e.UserViewModel
import com.example.blog_e.data.model.UpdateUserAPIModel
import com.example.blog_e.databinding.FragmentEditProfileBinding
import com.google.android.material.snackbar.Snackbar

class EditProfileFragment(): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        val userViewModel: UserViewModel by activityViewModels()

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (binding.editBio.text.toString() == "") {
                binding.editBio.setText(user?.selfDescription?: "")
            }

            if (binding.editDisplayName.text.toString() == "") {
                binding.editDisplayName.setText(user?.displayName ?: "")
            }
        }

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        binding.saveButton.setOnClickListener {
            val info = UpdateUserAPIModel(
                displayName = binding.editDisplayName.text.toString(),
                bio = binding.editBio.text.toString(),
                iconId = null,
                password = null
            )
            userViewModel.updateUserInfo(info).observe(viewLifecycleOwner) { response ->
                if (response.errorMessage == null) {
                    parentFragmentManager.beginTransaction().remove(this).commit()
                } else {
                    Snackbar.make(binding.root, response.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {}
}
