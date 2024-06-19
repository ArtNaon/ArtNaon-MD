package com.example.artnaon.ui.view.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.artnaon.R
import com.example.artnaon.databinding.FragmentProfileBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.main.MainViewModel
import com.example.artnaon.ui.view.profile.editprofile.EditProfileActivity
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
        binding.editButton.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE)
        }

        binding.termsLayout.setOnClickListener {
            showConfirmationDialog(
                "https://github.com/ArtNaon/ArtNaon-MD/blob/1568e48f267716b23caab8de70aac8745f128979/Terms%20of%20Service%20(ToS).md",
                "Are you sure you want to open the Terms of Service?"
            )
        }

        binding.aboutLayout.setOnClickListener {
            showConfirmationDialog(
                "https://github.com/ArtNaon/ArtNaon-MD/blob/1568e48f267716b23caab8de70aac8745f128979/About.md",
                "Are you sure you want to open the About page?"
            )
        }

        binding.suggetLayout.setOnClickListener {
            showConfirmationDialog(
                "mailto:artnaon07@gmail.com?subject=Suggestions%20for%20Improving%20ArtNaon",
                "Are you sure you want to send an email suggestion?"
            )
        }
    }

    private fun showConfirmationDialog(url: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Open Link")
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, _ ->
                openLink(url)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mainViewModel.getSession().observe(viewLifecycleOwner) { userModel ->
                if (userModel != null) {
                    viewModel.fetchUserDetails(userModel.email)
                }
            }
        }
    }

    private fun setupUserProfile() {
        mainViewModel.getSession().observe(viewLifecycleOwner) { userModel ->
            if (userModel != null) {
                viewModel.fetchUserDetails(userModel.email)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            if (userDetails != null) {
                binding.usernameTextView.text = userDetails.name
                binding.emailTextView.text = userDetails.email
                val pictureUrl = userDetails.picture
                if (pictureUrl != null && pictureUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(pictureUrl)
                        .placeholder(R.drawable.baseline_person_off_24)
                        .error(R.drawable.baseline_person_off_24)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.profileImageView)
                }
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

        // Simpan pilihan bahasa di SharedPreferences
        saveLanguageSetting(language)

        // Restart activity to apply changes
        val intent = Intent(requireContext(), requireActivity()::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun saveLanguageSetting(language: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("language_setting", language).apply()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pgProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val EDIT_PROFILE_REQUEST_CODE = 1
    }
}
