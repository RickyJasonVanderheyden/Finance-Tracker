package com.example.trackerfinance

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trackerfinance.data.TransactionRepository
import com.example.trackerfinance.databinding.ActivityMainBinding
import com.example.trackerfinance.utils.NotificationUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var transactionRepository: TransactionRepository
    private lateinit var viewModel: MainViewModel
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactionRepository = TransactionRepository(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Get NavController from NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the bottom navigation view with the nav controller
        binding.navView.setupWithNavController(navController)

        // Configure the action bar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_activity,
                R.id.navigation_budget,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup add transaction button click listener
        binding.navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_add -> {
                    navController.navigate(R.id.navigation_add)
                    true
                }
                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }

        // Create notification channel
        NotificationUtils.createNotificationChannel(this)

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Notification permission denied. Budget alerts won't work.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun refreshDisplayedAmounts() {
        // Refresh the current fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        
        // Recreate the current fragment to refresh all displayed amounts
        currentFragment?.let {
            navHostFragment.childFragmentManager.beginTransaction()
                .detach(it)
                .attach(it)
                .commit()
        }
    }
}