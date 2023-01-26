package com.example.blog_e.ui.editProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.R
import com.example.blog_e.UserViewModel
import com.example.blog_e.adapters.ProfilePictureAdapter
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.data.model.UpdateUserAPIModel
import com.example.blog_e.databinding.FragmentEditProfileBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class EditProfileFragment(): Fragment() {
    private val images: MutableList<ProfilePicture> = ProfilePicture.values().toMutableList()
    private var newIconId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val userViewModel: UserViewModel by activityViewModels()

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user == null) {
                return@observe
            }

            if (binding.editBio.text.toString() == "") {
                binding.editBio.setText(user.selfDescription?: "")
            }

            binding.avatarBtn.setImageResource(user.profilePicture.res)

            if (binding.editDisplayName.text.toString() == "") {
                binding.editDisplayName.setText(user.displayName)
            }
        }

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        // TODO: Same as in Signup, should not be repeated
        binding.avatarBtn.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_profile_picutres, null)
            val recyclerView =
                dialogView.findViewById<RecyclerView>(R.id.profile_images_list)

            recyclerView.layoutManager = FlexboxLayoutManager(binding.root.context)
            val builder = MaterialAlertDialogBuilder(binding.root.context)
                .setTitle("Select a profile picture")
                .setView(dialogView)
                .setNeutralButton(R.string.cancel) { dialoParam, _ ->
                    dialoParam.cancel()
                }

            val dialog = builder.create()
            dialog.show()
            val imagesAdapter = ProfilePictureAdapter(
                images,
                object : ProfilePictureAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        binding.avatarBtn.setImageResource(images[position].res)
                        newIconId = images[position].toString()
                        dialog.dismiss()
                    }
                }
            )
            recyclerView.adapter = imagesAdapter
        }

        binding.saveButton.setOnClickListener {
            var bio = binding.editBio.text.toString()
            if (bio.length > 100) {
                bio = bio.substring(0, 100)
            }

            var displayName: String? = binding.editDisplayName.text.toString()
            if (displayName == "") {
                displayName = null
            }

            val info = UpdateUserAPIModel(
                displayName = displayName,
                bio = bio,
                iconId = newIconId,
                password = null
            )

            binding.loadingOverlay.visibility = View.VISIBLE

            userViewModel.updateUserInfo(info).observe(viewLifecycleOwner) { response ->
                if (response.errorMessage == null) {
                    parentFragmentManager.beginTransaction().remove(this).commit()
                } else {
                    Snackbar.make(binding.root, response.errorMessage, Toast.LENGTH_SHORT).show()
                }
                binding.loadingOverlay.visibility = View.INVISIBLE
            }
        }

        return binding.root
    }
}
