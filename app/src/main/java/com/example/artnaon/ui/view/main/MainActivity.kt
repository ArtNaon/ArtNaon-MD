package com.example.artnaon.ui.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.artnaon.R
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.databinding.ActivityMainBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.camera.CameraActivity
import com.example.artnaon.ui.view.profile.ProfileViewModel
import com.example.artnaon.ui.view.splash.SplashActivity
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupSwitchMode()
        setupAction()

        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        val navView: ChipNavigationBar = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setOnItemSelectedListener { id ->
            when (id) {
                R.id.navigation_home -> navController.navigate(R.id.navigation_home)
                R.id.navigation_profile -> navController.navigate(R.id.navigation_profile)
            }
        }
    }

    private fun setupSwitchMode() {
        profileViewModel.themeSetting.observe(this, Observer { isDarkModeActive ->
            val mode = if (isDarkModeActive) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        })
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupAction() {
        viewModel.getSession().observe(this) {
            if (!it.isLogin) {
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                ApiConfig().setToken(it.token)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
