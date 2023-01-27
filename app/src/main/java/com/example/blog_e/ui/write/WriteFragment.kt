package com.example.blog_e.ui.write

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.data.model.AutogenrationOptions
import com.example.blog_e.data.model.AutoCompleteOptions
import com.example.blog_e.data.model.Post
import com.example.blog_e.databinding.FragmentWriteBinding
import com.example.blog_e.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
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
     
        binding.autoCompleteOptions.visibility = View.GONE

        binding.lifecycleOwner = this
        Log.v(TAG, Utils.formatBackstack(findNavController()))

        fun displayGeneratedContent(view: TextInputEditText, content: String, range: LongRange){
            val prefix = view.text.toString()
            val completed = prefix + " " + content
            val span = SpannableString(completed)

            span.setSpan(
                ForegroundColorSpan(Color.LTGRAY),
                prefix.length, completed.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            view.setText(span)
            var i = prefix.length
            var handler = Handler()
            var runnable = object : Runnable {
                override fun run() {
                    span.setSpan(ForegroundColorSpan(Color.BLACK),
                        0, i,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    view.setText(span)
                    if (i++ < completed.length){
                        handler.postDelayed(this, range.random())
                    }
                }
            }
            handler.postDelayed(runnable, 0)
            view.setSelection(view.length())
        }

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
                          displayGeneratedContent(binding.postInput, it.generatedText, Config.generatePostDelay)
                        }
                    }
                }
            }
        }
        


        fun getMood(): String {
            var mood : String

            try { mood = binding.root.findViewById<Button>(
                binding.emotionButtonsGroup.checkedButtonId).text.toString().lowercase()}
            catch (e: NullPointerException){ mood = Config.defaultMood}

            return mood
        }

        postBtn.setOnClickListener {
            Log.e(TAG, getMood())
            writeViewModel.createPost(
                Post(
                    content=binding.postInput.text.toString(),
                    autogenerateResponses = binding.autoReplyFlag.isChecked
                ),
                AutogenrationOptions(mood = getMood(), 
                    temperature = binding.writeGenerateTemperature.value, 
                    historyLength = binding.autpreplyHistoryLength.value.toInt())
            )
        }

        generateEmptyBtn.setOnClickListener {
            writeViewModel.completePost(
                AutoCompleteOptions(
                    binding.postInput.text.toString(),
                    binding.writeGenerateTemperature.value,
                    getMood(), "true")
            )
        }


        binding.autoReplyFlag.setOnCheckedChangeListener {_, isChecked ->
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