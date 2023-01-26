package com.example.blog_e.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.add
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.blog_e.R
import com.example.blog_e.adapters.PostAdapter
import com.example.blog_e.databinding.FragmentHomeBinding
import com.example.blog_e.ui.VisitProfile.VisitProfileFragment
import com.example.blog_e.utils.PostComparator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment(private val openFragment: (Fragment) -> Unit) : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

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
                val detailFragment = VisitProfileFragment.newInstance(username)

                // TODO: Passing this lambda is not optimal.
                openFragment(detailFragment)
            }
        }
        val postAdapter = PostAdapter(PostComparator(), binding.root.context, navigateToProfile)
        val linearLayoutManager = LinearLayoutManager(context)

        val recyclerView = binding.postsListRecyclerView
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = postAdapter
        recyclerView.setHasFixedSize(true)

        val isUserFeed = arguments?.getBoolean(IS_USER_FEED_KEY) ?: false
        viewModel.getPosts(isUserFeed).observe(viewLifecycleOwner) {
            postAdapter.submitData(lifecycle, it)
        }

        binding.swipeRefresh.setOnRefreshListener {
            // TODO: Den Adapter zu refreshen sorgt aktuell dafür, dass hier das Paging nicht richtig funktioniert. Nochmal genauer naschschauen!!!
            // postAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }

        return binding.root
    }
}

private val TABS = listOf("Bubble", "Global")

class HomePagerFragment(): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = HomePagerAdapter(this) { openFragment(it) }
        val viewPager: ViewPager2 = view.findViewById(R.id.pager)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position < TABS.count()) {
                tab.text = TABS[position]
            }
        }.attach()
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, fragment)
            .addToBackStack(null)
            .setReorderingAllowed(true)
            .commit()
    }
}

class HomePagerAdapter(fragment: Fragment, private val openFragment: (Fragment) -> Unit): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = TABS.count()

    override fun createFragment(position: Int): Fragment {
        val fragment = HomeFragment(openFragment)
        fragment.arguments = Bundle().apply {
            putBoolean(IS_USER_FEED_KEY, position == 0)
        }
        return fragment
    }
}

private const val IS_USER_FEED_KEY = "isUserFeed"
