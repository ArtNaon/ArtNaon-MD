// ProfileFragment
package com.example.artnaon.ui.view.profile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.artnaon.R
import com.example.artnaon.databinding.FragmentProfileBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.main.MainViewModel
import com.example.artnaon.ui.view.profile.mypost.MyPostActivity
import com.example.artnaon.ui.view.profile.save.SaveActivity
import com.example.artnaon.ui.view.welcome.WelcomeActivity
import java.util.Locale

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

        binding.savePostProfile.setOnClickListener {
            startActivity(Intent(activity, SaveActivity::class.java))
        }
    }

    private fun setupUserProfile() {
        mainViewModel.getSession().observe(viewLifecycleOwner) { userModel ->
            if (userModel != null) {
                viewModel.fetchUserDetails(userModel.email)
            }
        }

        viewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            if (userDetails != null) {
                binding.usernameTextView.text = userDetails.name
                binding.emailTextView.text = userDetails.email
                Glide.with(this)
                    .load(userDetails.picture)
                    .into(binding.profileImageView)
            }
        }
    }

    private fun setupLogout() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        viewModel.logoutState.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut == true) {
                val intent = Intent(requireActivity(), WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun setupSwitchMode() {
        viewModel.themeSetting.observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.darkmodeSwitch.isChecked = isDarkModeActive
        }

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
