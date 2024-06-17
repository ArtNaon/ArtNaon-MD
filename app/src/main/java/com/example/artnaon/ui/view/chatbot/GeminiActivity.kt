package com.example.artnaon.ui.view.chatbot

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.artnaon.R
import com.example.artnaon.data.di.Injection
import com.example.artnaon.data.pref.MessageModel
import com.example.artnaon.data.pref.loadMessages
import com.example.artnaon.data.pref.saveMessages
import com.example.artnaon.databinding.ActivityGeminiBinding
import com.example.artnaon.databinding.ItemMessageBinding
import com.example.artnaon.ui.ViewModelFactory
import com.example.artnaon.ui.view.main.MainViewModel
import com.example.artnaon.ui.view.profile.ProfileViewModel
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GeminiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeminiBinding
    private val viewModel by viewModels<GeminiViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var userProfilePicture: String? = null
    private var userName: String? = null
    private var userEmail: String? = null
    private val messages = mutableListOf<MessageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeminiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChatbotBack.setOnClickListener {
            finish()
        }

        setupViewModel()
        setupKeyboard()
    }

    private fun setupViewModel() {
        mainViewModel.getSession().observe(this, Observer { userModel ->
            if (userModel != null) {
                userProfilePicture = userModel.picture
                userName = userModel.name
                userEmail = userModel.email
                profileViewModel.fetchUserDetails(userModel.email)

                userEmail?.let { email ->
                    messages.addAll(loadMessages(this, email))
                    messages.forEach { message ->
                        populateChatBody(message.username, message.message, message.date, message.userProfilePicture)
                    }
                }
            }
        })

        profileViewModel.userDetails.observe(this, Observer { userDetails ->
            if (userDetails != null) {
                userName = userDetails.name
                userProfilePicture = userDetails.picture
            }
        })

        binding.btnSend.setOnClickListener {
            val string = binding.edtInput.text?.toString().orEmpty()
            binding.pgChatbot.visibility = View.VISIBLE

            binding.edtInput.setText("")
            val date = getDate()
            val message = MessageModel(userName ?: "User", string, date, userProfilePicture)
            messages.add(message)
            userEmail?.let { email ->
                saveMessages(this, email, messages)
            }
            populateChatBody(userName ?: "User", string, date, userProfilePicture)

            viewModel.sendMessage(string)
        }

        viewModel.response.observe(this, Observer { response ->
            binding.pgChatbot.visibility = View.GONE
            val date = getDate()
            val message = MessageModel("ArtNaon Bot", response, date, "url_to_bot_profile_picture") // Ganti dengan URL foto profil bot jika ada
            messages.add(message)
            userEmail?.let { email ->
                saveMessages(this, email, messages)
            }
            populateChatBody("ArtNaon Bot", response, date, "url_to_bot_profile_picture")
        })
    }

    private fun setupKeyboard() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            binding.geminiLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = imeInsets.bottom
            }
            insets
        }

        binding.edtInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun populateChatBody(username: String, message: String, date: String, profilePicture: String?) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemBinding = ItemMessageBinding.inflate(inflater, null, false)

        itemBinding.tvUsername.text = username
        itemBinding.tvUserBot.text = message
        itemBinding.tvDate.text = date

        if (username == "ArtNaon Bot") {
            itemBinding.ivChatbot.setImageResource(R.drawable.logo_art)
        } else {
            if (profilePicture != null && profilePicture.isNotEmpty()) {
                Glide.with(this).load(profilePicture).into(itemBinding.ivChatbot)
            } else {
                itemBinding.ivChatbot.setImageResource(R.drawable.baseline_person_off_24)
            }
        }
        binding.chatbotLayout.addView(itemBinding.root)

        val scrollView = binding.scrollChatbot
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    @SuppressLint("NewApi")
    private fun getDate(): String {
        val instant = Instant.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm").withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
}