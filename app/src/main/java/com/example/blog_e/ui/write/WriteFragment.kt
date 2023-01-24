package com.example.blog_e.ui.write

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
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


@AndroidEntryPoint
class WriteFragment() : Fragment() {

    private val TAG = Config.tag(this.toString())
    private var _binding: FragmentWriteBinding? = null

    private val binding get() = _binding!!


    // This property is only valid between onCreateView and
    // onDestroyView.

    private val writeViewModel: WriteViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(tag, "created")
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentWriteBinding.inflate(inflater, container, false)

        val postBtn = binding.postButton
        val generateEmptyBtn = binding.generatePostFromPromptButton
        val spinner = binding.completeSpinner
        val postInput = binding.postInput
        val generationTemperature = binding.writeGenerateTemperature
        val moodChoices = binding.emotionButtonsGroup
        val autoReplyFlag = binding.autoReplyFlag
        binding.autoCompleteOptions.visibility = View.GONE

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
                        if (it.success)  {
                            val prefix = postInput.text.toString()
                            val generated = it.generatedText
                            val completed = prefix + " " + generated
                            val span = SpannableString(completed)

                            span.setSpan(
                                ForegroundColorSpan(Color.LTGRAY),
                                prefix.length, completed.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            postInput.setText(span)
                            var i = prefix.length
                            var handler = Handler()
                            var runnable = object : Runnable {
                                override fun run() {
                                    span.setSpan(ForegroundColorSpan(Color.BLACK),
                                        0, i,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                    postInput.setText(span)
                                    if (i++ < completed.length){
                                        handler.postDelayed(this, Config.generatePostDelay.random())
                                    }
                                }
                            }
                            handler.postDelayed(runnable, 0)
                            postInput.setSelection(postInput.length())
                        }
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


        autoReplyFlag.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) {
               binding.autoCompleteOptions.visibility = View.VISIBLE
            } else {
                binding.autoCompleteOptions.visibility = View.GONE
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}