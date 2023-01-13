package com.example.blog_e.ui.write

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.data.model.CompletePayload
import com.example.blog_e.data.model.Post
import com.example.blog_e.data.model.User
import com.example.blog_e.data.repository.BlogPostRepository
import com.example.blog_e.data.repository.UserRepo
import com.example.blog_e.databinding.FragmentHomeBinding
import com.example.blog_e.databinding.FragmentWriteBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class WriteFragment() : Fragment() {

    private val TAG = Config.tag(this.toString())
    private var _binding: FragmentWriteBinding? = null

    private val binding get() = _binding!!


    // This property is only valid between onCreateView and
    // onDestroyView.

    private val writeViewModel: WriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(tag, "created")
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentWriteBinding.inflate(inflater, container, false)


        val postBtn = binding.postButton
        val generatePromptBtn = binding.generatePostFromNothingButton
        val generateEmptyBtn = binding.generatePostFromPromptButton
        val aiSwitch = binding.aiSwitchButton
        val spinner = binding.completeSpinner
        val postInput = binding.postInput
        val generationTemperature = binding.writeGenerateTemperature
        val moodChoices = binding.emotionButtonsGroup
        val autoReplyFlag = binding.autoReplyFlag

        binding.lifecycleOwner = this



        viewLifecycleOwner.lifecycleScope.launch {
            writeViewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    Log.d(TAG, "hi collecting state: " + it.postSuccesful.toString())
                    if (it.postSuccesful){
                        findNavController().navigate(R.id.navigation_home)
                    }
                    if (it.running){
                        spinner.visibility = View.VISIBLE
                    }
                    else {
                        spinner.visibility = View.GONE
                    }
                    if (! it.success){
                        Snackbar.make(binding.root, it.error, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        postInput.setText(it.postInput)
                    }
                }
        }



        // add text watcher for empty input
        postInput.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().isNotBlank()) {
                        postBtn.isEnabled = true
                        generatePromptBtn.visibility = View.GONE
                        generateEmptyBtn.isEnabled = true

                    } else {

                        postBtn.isEnabled = false
                        generateEmptyBtn.isEnabled = false
                        generatePromptBtn.visibility = View.VISIBLE
                    }
                }
            }
        )

        aiSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.aiOptions.visibility = View.VISIBLE
                generateEmptyBtn.visibility = View.VISIBLE
                generatePromptBtn.visibility = View.VISIBLE
                binding.aiOptions.visibility = View.VISIBLE
            } else {
                binding.aiOptions.visibility = View.GONE
                generateEmptyBtn.visibility = View.GONE
                generatePromptBtn.visibility = View.GONE
                binding.aiOptions.visibility = View.GONE
            }
        }

        postBtn.setOnClickListener {
            Log.e(TAG, moodChoices.checkedButtonId.toString())
            writeViewModel.createPost(
                Post(
                    content=postInput.text.toString(),
                    autogenerateResponses = autoReplyFlag.isChecked
                    )
            )
        }


        generateEmptyBtn.setOnClickListener {
            Log.e(TAG, moodChoices.checkedButtonId.toString())
            writeViewModel.completePost(
                CompletePayload(
                    postInput.text.toString(),
                    generationTemperature.values[0],
                    "lucid", "true")
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}