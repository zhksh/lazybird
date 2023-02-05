package com.example.blog_e

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.blog_e.databinding.ActivityMainBinding
import com.example.blog_e.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


/**
 * This class extends the **AppCompatActivity** class and is annotated with **@AndroidEntryPoint**.
 * It is the main screen of this app and serves as the entry point for navigating to other screens
 * of the app.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(application)

        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in HIDE_NAV_BAR_FRAGMENTS) {
                navView.visibility = View.GONE
            } else {

                navView.visibility = View.VISIBLE
            }
        }

        navController.navigate(R.id.start_fragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    companion object {
        private val HIDE_NAV_BAR_FRAGMENTS = listOf(
            R.id.start_fragment,
            R.id.login_fragment,
            R.id.sign_up_fragment,
            R.id.navigation_visit_profile
        )
    }
}
