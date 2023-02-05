package com.example.blog_e.ui.write

import android.annotation.SuppressLint
import android.os.Bundle
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

enum class TemperatureLabel(val start: Float, val end: Float) {

    MILD(0.0f, 0.2f),
    MODERATE(0.2f, 0.4f),
    INTENSE(0.4f, 0.6f),
    WILD(0.6f, 0.8f),
    QUIXOTIC(0.8f, 1.0f);

    companion object {
        fun getLabelByValue(value: Float): String {
            return TemperatureLabel.values().filter { it -> scale(value) in it.start..it.end }
                .first().name
        }

        fun scale(value: Float): Float {
            return value / 2
        }
    }
}

/**
 * This class extends the **Fragment** class and is annotated with **@AndroidEntryPoint**. It
 * provides the implementation for creating a post.
 *
 * The user can enter the text for the post, select a mood and set options for auto-generating
 * responses. The post can also be auto-completed by generating text suggestions.
 *
 * The fragment uses the WriteViewModel to communicate with the backend to create a post and get
 * auto-complete suggestions.
 */
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


        binding.writeGenerateTemperature.setLabelFormatter { value: Float ->
            return@setLabelFormatter "${TemperatureLabel.getLabelByValue(value)}"
        }

        fun getMood(): String {
            binding.root.findViewById<Button>(binding.emotionButtonsGroup.checkedButtonId)
                ?.let { mmodbtn ->
                    return mmodbtn.text.toString().lowercase()
                }
            return Config.defaultMood
        }

        binding.postButton.setOnClickListener {
            val post = Post(
                content = binding.postInput.text.toString(),
                autogenerateResponses = binding.autoReplyFlag.isChecked
            )
            val params = AutogenrationOptions(
                mood = getMood(),
                temperature = binding.writeGenerateTemperature.value,
                historyLength = binding.autpreplyHistoryLength.value.toInt()
            )
            writeViewModel.createPost(post, params).observe(viewLifecycleOwner) { res ->
                if (res.errorMessage == null)
                    findNavController().navigate(R.id.action_write_fragment_to_nav_host_fragment_activity_main)
                else Snackbar.make(binding.root, res.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        binding.generatePostFromPromptButton.setOnClickListener {
            val params = AutoCompleteOptions(
                binding.postInput.text.toString(),
                binding.writeGenerateTemperature.value,
                getMood()
            )
            val garbler = Garbler(binding.postInput, Config.generatePostDelay)
            garbler.garble()
            writeViewModel.completePost(params).observe(viewLifecycleOwner) { res ->
                garbler.cancel()
                if (res.errResponse == null) {
                    if (res.generatedText.isBlank()) {
                        Snackbar.make(
                            binding.root,
                            "Maybe be a little more creative ..",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else garbler.rebuildStringWithPrefix(res.generatedText)
                } else {
                    Snackbar.make(
                        binding.root,
                        res.errResponse.errorMessage.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.autoReplyFlag.setOnCheckedChangeListener { _, isChecked ->
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