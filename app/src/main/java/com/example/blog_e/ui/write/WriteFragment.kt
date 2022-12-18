package com.example.blog_e.ui.write

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.blog_e.databinding.FragmentWriteBinding

class WriteFragment : Fragment() {

    private var _binding: FragmentWriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val writeViewModel =
            ViewModelProvider(this).get(WriteViewModel::class.java)

        _binding = FragmentWriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val postBtn = binding.postButton
        postBtn.isEnabled = false

        val aiSwitch = binding.aiSwitchButton
        val postInput = binding.postInput
        val emotionButtonsGroup = binding.emotionButtonsGroup

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
                    postBtn.isEnabled = s.toString().isNotBlank()
                }
            }

        )

        aiSwitch.setOnCheckedChangeListener { _, isChecked -> println("Switch state=$isChecked") }


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}