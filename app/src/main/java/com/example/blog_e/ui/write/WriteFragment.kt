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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.data.model.CompletePayload
import com.example.blog_e.data.model.Post
import com.example.blog_e.databinding.FragmentWriteBinding
import com.example.blog_e.utils.Utils
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.NullPointerException

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
        Log.v(TAG, Utils.formatBackstack(findNavController()))


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    writeViewModel.uiState.collect {
                        Log.v(TAG, "collecting ui state: ${it.toString()}")
                        if (it.success){
                            findNavController().navigate(R.id.action_write_fragment_to_nav_host_fragment_activity_main)
                        }
                        if (it.running){
                            spinner.visibility = View.VISIBLE
                        }
                        else {
                            spinner.visibility = View.GONE
                            if (!it.success){
                                if (it.errorMsg.isNotBlank())
                                    Snackbar.make(binding.root, it.errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                   }
                }

                launch {
                    writeViewModel.generateState.collect {
                        Log.v(TAG, "collecting generation state: ${it.toString()}")
                        if (it.success)  postInput.setText(it.generatedText)
                    }
                }
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
            var mood : String
            try { mood = binding.root.findViewById<Button>(moodChoices.checkedButtonId).text.toString().lowercase()}
            catch (e: NullPointerException){ mood = Config.defaultMood}

            writeViewModel.completePost(
                CompletePayload(
                    postInput.text.toString(),
                    generationTemperature.value,
                    mood, "false")
            )
        }

        postInput.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}