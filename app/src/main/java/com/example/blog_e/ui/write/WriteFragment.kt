package com.example.blog_e.ui.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        val textView: TextView = binding.textWrite
        writeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}