package com.example.artnaon.ui.view.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.artnaon.R
import com.example.artnaon.databinding.FragmentProfileBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.main.MainViewModel
import com.example.artnaon.ui.view.profile.mypost.MyPostActivity
import com.example.artnaon.ui.view.welcome.WelcomeActivity
import java.util.*

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLogout()
        setupSwitchMode()
        setupUserProfile()
        setupLanguageSelection()

        binding.myPostProfile.setOnClickListener {
            startActivity(Intent(activity, MyPostActivity::class.java))
        }
    }

    private fun setupUserProfile() {
        mainViewModel.getSession().observe(viewLifecycleOwner) { userModel ->
            if (userModel != null) {
                binding.usernameTextView.text = userModel.name
                binding.emailTextView.text = userModel.email
            }
        }
    }

    private fun setupLogout() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        viewModel.logoutState.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val intent = Intent(requireActivity(), WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        })
    }

    private fun setupSwitchMode() {
        viewModel.themeSetting.observe(viewLifecycleOwner, Observer { isDarkModeActive ->
            binding.darkmodeSwitch.isChecked = isDarkModeActive
        })

        binding.darkmodeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }
    }

    private fun setupLanguageSelection() {
        binding.languageCardlayout.setOnClickListener {
            showLanguageSelectionDialog()
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Indonesian")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_language)
            .setItems(languages) { _, which ->
                when (which) {
                    0 -> setLocale("en")
                    1 -> setLocale("id")
                }
            }
        builder.create().show()
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)
        // Restart activity to apply changes
        val intent = Intent(requireContext(), requireActivity()::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
