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
import com.example.blog_e.ui.profile.ProfileFragment
import com.example.blog_e.utils.Garbler
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class EditProfileFragment : Fragment() {
    private val images: MutableList<ProfilePicture> = ProfilePicture.values().toMutableList()
    private var newIconId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val userViewModel: UserViewModel by activityViewModels()

        if (binding.editBio.text.toString() == "") {
            binding.editBio.setText(userViewModel.getUser().value?.selfDescription ?: "")
        }


        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user == null) {
                return@observe
            }
            binding.avatarBtn.setImageResource(user.profilePicture.res)

            if (binding.editDisplayName.text.toString().isBlank()) {
                binding.editDisplayName.setText(user.displayName)
            }
        }

        binding.cancelButton.setOnClickListener {
            close()
        }


        binding.avatarBtn.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_profile_picutres, null)
            val recyclerView =
                dialogView.findViewById<RecyclerView>(R.id.profile_images_list)

            val layoutManager = FlexboxLayoutManager(binding.root.context)
            recyclerView.layoutManager = layoutManager
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
            bio = bio.substring(0, minOf(bio.length, com.example.blog_e.Config.maxDescLength))

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
                    close()
                } else {
                    Snackbar.make(binding.root, response.errorMessage, Toast.LENGTH_SHORT).show()
                }
                binding.loadingOverlay.visibility = View.INVISIBLE
            }
        }

        binding.generateButton.setOnClickListener {
            val garbler = Garbler(binding.editBio, com.example.blog_e.Config.generatePostDelay)
            garbler.garble()
            userViewModel.createSelfDesc().observe(viewLifecycleOwner) { response ->
                garbler.cancel()
                if (response.err == null) {
                    garbler.rebuildString(response.bio)
                } else Snackbar.make(
                    binding.root,
                    response.err.errorMessage.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        return binding.root
    }

    private fun close() {
        val fragment = ProfileFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, fragment)
            .addToBackStack(null)
            .commit()
    }


}
