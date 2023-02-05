package com.example.blog_e.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.blog_e.R
import com.example.blog_e.ui.common.PostAdapter
import com.example.blog_e.databinding.FragmentHomeBinding
import com.example.blog_e.utils.PostComparator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

/**
 * This class extends the **Fragment** class and is annotated with **@AndroidEntryPoint**. It
 * provides the implementation for displaying posts in a recyclerview.
 *
 * The fragment is displayed in the *HomePagerFragment* that selects the filter of whether this
 * fragment displays posts of a global feed or the user's bubble.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

    private val startForPostThreadResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            // No need for handling the data; always refresh the adapter when coming back
            //it.data?.let { intent -> }
            postAdapter.refresh()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        val navigateToProfile: (String) -> Unit = { username ->
            if (username == viewModel.username) {
                findNavController().navigate(R.id.navigation_profile)
            } else {
                findNavController().navigate(
                    R.id.action_navigation_home_to_navigation_visit_profile,
                    bundleOf("username" to username)
                )
            }
        }
        postAdapter = PostAdapter(
            PostComparator(),
            requireActivity(),
            startForPostThreadResult,
            navigateToProfile
        )
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView = binding.postsListRecyclerView
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = postAdapter
        recyclerView.setHasFixedSize(true)

        val isUserFeed = arguments?.getBoolean(IS_USER_FEED_KEY) ?: false
        viewModel.getPosts(isUserFeed).observe(viewLifecycleOwner) {
            postAdapter.submitData(lifecycle, it)
        }

        binding.swipeRefresh.setColorSchemeResources(R.color.secondaryColor)
        binding.swipeRefresh.setOnRefreshListener {
            postAdapter.refresh()
            recyclerView.smoothScrollToPosition(0)
            binding.swipeRefresh.isRefreshing = false
        }

        val fab: FloatingActionButton = binding.fab

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 || linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                    fab.hide()
                } else if (dy < 0) {
                    fab.show()
                }
            }
        })

        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        return binding.root
    }
}

private val TABS = listOf("Global", "Bubble")

/**
 * This class extends the **Fragment** class and selects the current *HomeFragment*.
 */
class HomePagerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = HomePagerAdapter(this)
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position < TABS.count()) {
                tab.text = TABS[position]
            }
        }.attach()
    }
}

class HomePagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = TABS.count()

    override fun createFragment(position: Int): Fragment {
        val fragment = HomeFragment()
        fragment.arguments = Bundle().apply {
            putBoolean(IS_USER_FEED_KEY, position == 1)
        }
        return fragment
    }
}

private const val IS_USER_FEED_KEY = "isUserFeed"
