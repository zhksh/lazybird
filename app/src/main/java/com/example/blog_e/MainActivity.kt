package com.example.blog_e

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.blog_e.databinding.ActivityMainBinding
import com.example.blog_e.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

var HIDE_NAV_BAR_FRAGMENTS = listOf(R.id.start_fragment, R.id.login_fragment, R.id.sign_up_fragment)

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(application)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_write,
                R.id.navigation_profile,
                R.id.navigation_search
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            if (destination.id in HIDE_NAV_BAR_FRAGMENTS) {
//                navView.visibility = View.GONE
//            } else {
//
//                navView.visibility = View.VISIBLE
//            }
//        }

        if (sessionManager.fetchAuthToken() != null) {
//            Snackbar.make(
//                binding.root,
//                "Welcome back!",
//                Snackbar.LENGTH_LONG
//            ).show()
            navController.navigate(R.id.navigation_home)
        }
    }
}