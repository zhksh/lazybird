package com.example.blog_e.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.blog_e.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //create variables.
    lateinit var historyList: ArrayList<String>
    lateinit var trendList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //init the view
        val searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textSearch
        searchViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        //Bind some views about search history
        val itemEdt = binding.editView
        val listView: ListView = binding.historyList
        val imageButton: ImageButton = binding.imageButton
        historyList = ArrayList()

        //Bind some views about trends
        val listView2: ListView = binding.trendList
        trendList = ArrayList()
        trendList.add("Elon Musk")
        trendList.add("Tesla")

        //initialize adapter for our list view.
        val adapter: ArrayAdapter<String?> = ArrayAdapter<String?>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            historyList as List<String?>
        )
        val adapter2: ArrayAdapter<String?> = ArrayAdapter<String?>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            trendList as List<String?>
        )


        //set adapter for our list view
        listView.adapter = adapter
        listView2.adapter = adapter2

        //add click listener for our button.
        imageButton.setOnClickListener {

            //intent to results
            Toast.makeText(this.context, "Search Results Page not Ready!", Toast.LENGTH_SHORT).show()
            //get text from edit text
            val item = itemEdt.text.toString()
            //check if item is not empty
            if (item.isNotEmpty()) {
                //add item to our list.
                historyList.add(item)
                //notify adapter
                //that data in list is updated to update our list view.
                adapter.notifyDataSetChanged()
            }
        }

        //item click events
        //TODO

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}