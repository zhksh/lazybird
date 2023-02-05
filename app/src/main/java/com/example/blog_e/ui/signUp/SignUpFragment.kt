package com.example.blog_e.ui.signUp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.ui.common.UserViewModel
import com.example.blog_e.adapters.ProfilePictureAdapter
import com.example.blog_e.data.model.NewUserAPIModel
import com.example.blog_e.data.model.ProfilePicture
import com.example.blog_e.databinding.FragmentSignUpBinding
import com.example.blog_e.utils.validatePassword
import com.example.blog_e.utils.validateUsername
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * This class extends the **Fragment** class and is annotated with **@AndroidEntryPoint**.
 * It is a fragment class that is used for displaying the sign up form for the user.
 *
 * The main purpose of this class is the sign up operation.
 *
 * Sign up form fields include:
 *   - a selection for a profile picture
 *   - a username
 *   - a display name (default is username)
 *   - a password
 *
 * You can also navigate to the login screen.
 */
@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val TAG = Config.tag(this.toString())

    private val images: MutableList<ProfilePicture> = ProfilePicture.values().toMutableList()
    private var imagePos: Int? = null

    private lateinit var dialog: AlertDialog

    val viewModel: SignUpViewModel by viewModels()
    val userViewModel: UserViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        setupBinding()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBinding() {
        binding.viewModel = viewModel

        binding.btnAccount.setOnClickListener {
            findNavController().navigate(R.id.login_fragment)
        }

        binding.btnSignUp.setOnClickListener {
            signup(viewModel, userViewModel, binding)
        }

        binding.etPassword.editText?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signup(viewModel, userViewModel, binding)
                val imm =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.avatarBtn.setImageResource(DEFAULT_PROFILE_PICTURE.res)

        binding.avatarBtn.setOnClickListener {
            val imagesAdapter = ProfilePictureAdapter(
                images,
                object : ProfilePictureAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        Log.i(TAG, "Picture at index $position was selected")
                        updateProfilePicture(position)
                        dialog.dismiss()
                    }
                }
            )
            val dialogView = layoutInflater.inflate(R.layout.dialog_profile_picutres, null)
            val recyclerView =
                dialogView.findViewById<RecyclerView>(R.id.profile_images_list)

            recyclerView.layoutManager = FlexboxLayoutManager(binding.root.context)
            recyclerView.adapter = imagesAdapter
            val builder = MaterialAlertDialogBuilder(binding.root.context)
                .setTitle("Select a profile picture")
                .setView(dialogView)
                .setNeutralButton(R.string.cancel) { dialoParam, _ ->
                    dialoParam.cancel()
                }
            dialog = builder.create()
            dialog.show()
        }
    }

    private fun signup(
        viewModel: SignUpViewModel,
        userViewModel: UserViewModel,
        binding: FragmentSignUpBinding
    ) {
        viewModel.isLoading.set(true)

        val username = binding.etUserName.editText?.text.toString()
        val displayName = binding.etDisplayName.editText?.text.toString()
        val password = binding.etPassword.editText?.text.toString()

        val error = validateInput(username, password, displayName)
        if (error != "") {
            Snackbar.make(binding.root, error, Toast.LENGTH_SHORT).show()
            viewModel.isLoading.set(false)
            return
        }

        val payload = NewUserAPIModel(
            displayName = displayName,
            password = password,
            username = username,
            iconId = getProfilePicture(),
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

    private fun getProfilePicture(): String {
        return if (imagePos != null) {
            images[imagePos!!].toString()
        } else {
            // if non is selected, choose default
            DEFAULT_PROFILE_PICTURE.toString()
        }
    }

    private fun updateProfilePicture(imgIdx: Int) {
        imagePos = imgIdx
        val currentImageRes = images[imgIdx].res
        binding.avatarBtn.setImageResource(currentImageRes)
    }

    private fun validateInput(username: String, password: String, displayName: String): String {
        val err = validateUsername(username)
        if (err != "") {
            return err
        }

        if (displayName == "") {
            return "Please enter a display name"
        }

        return validatePassword(password)
    }

    companion object {
        private val DEFAULT_PROFILE_PICTURE = ProfilePicture.PICTURE_05
    }
}
