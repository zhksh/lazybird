package com.example.blog_e.ui.write

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.blog_e.databinding.FragmentWriteBinding

class WriteFragment : Fragment() {

    private var _binding: FragmentWriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val writeViewModel: WriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val postBtn = binding.postButton
        val generatePromptBtn = binding.generatePostFromNothingButton
        val generateEmptyBtn = binding.generatePostFromPromptButton
        val aiSwitch = binding.aiSwitchButton
        val postInput = binding.postInput


        writeViewModel.postText.observe(viewLifecycleOwner) { post ->
            postInput.text = SpannableStringBuilder(post)
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
            writeViewModel.savePost()
        }

        generateEmptyBtn.setOnClickListener {
            postInput.text = SpannableStringBuilder(postInput.text.toString() + "\nGenerated text")
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}