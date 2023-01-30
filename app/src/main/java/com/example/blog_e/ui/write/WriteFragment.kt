package com.example.blog_e.ui.write

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blog_e.Config
import com.example.blog_e.R
import com.example.blog_e.data.model.AutoCompleteOptions
import com.example.blog_e.data.model.AutogenrationOptions
import com.example.blog_e.data.model.Post
import com.example.blog_e.databinding.FragmentWriteBinding
import com.example.blog_e.utils.Garbler
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WriteFragment() : Fragment() {

    private val TAG = Config.tag(this.toString())
    private var _binding: FragmentWriteBinding? = null

    private val binding get() = _binding!!
    private val writeViewModel: WriteViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = FragmentWriteBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        binding.autoCompleteOptions.visibility = View.GONE


        fun getMood(): String {
            binding.root.findViewById<Button>(binding.emotionButtonsGroup.checkedButtonId)?.let { mmodbtn ->
                return mmodbtn.text.toString().lowercase()
            }
            return Config.defaultMood
        }

        binding.postButton.setOnClickListener {
            val post = Post(
                content=binding.postInput.text.toString(),
                autogenerateResponses = binding.autoReplyFlag.isChecked
            )
            val params = AutogenrationOptions(mood = getMood(),
                temperature = binding.writeGenerateTemperature.value,
                historyLength = binding.autpreplyHistoryLength.value.toInt())
            writeViewModel.createPost(post, params).observe(viewLifecycleOwner){res ->
                if (res.errorMessage == null)
                    findNavController().navigate(R.id.action_write_fragment_to_nav_host_fragment_activity_main)
                else Snackbar.make(binding.root, res.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.generatePostFromPromptButton.setOnClickListener {
            val params =  AutoCompleteOptions(
                binding.postInput.text.toString(),
                binding.writeGenerateTemperature.value,
                getMood())
            var garbler = Garbler(binding.postInput, Config.generatePostDelay)
            garbler.garble()
            writeViewModel.completePost(params).observe(viewLifecycleOwner){ res ->
                if (res.errResponse == null) {
                    if (res.generatedText.isBlank()){
                        garbler.cancel()
                        Snackbar.make(binding.root, "Maybe be a little more creative ..", Toast.LENGTH_SHORT).show()
                    }
                    else garbler.rebuildStringWithPrefix(res.generatedText)
                }
                else Snackbar.make(binding.root, res.errResponse.errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.autoReplyFlag.setOnCheckedChangeListener {_, isChecked ->
            if (isChecked) binding.autoCompleteOptions.visibility = View.VISIBLE
            else binding.autoCompleteOptions.visibility = View.GONE
        }
        

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}