package com.example.artnaon.ui.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.artnaon.R
import com.example.artnaon.databinding.ActivityWelcomeBinding
import com.example.artnaon.ui.view.signin.SignInActivity
import com.example.artnaon.ui.view.signup.SignUpActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAction()
    }

    override fun onResume() {
        super.onResume()
        playAnimation()
    }
    private fun playAnimation() {
        binding.btnWelcomeNewHere.alpha = 0f
        binding.tvWelcomeSignin.alpha = 0f

        val signup = ObjectAnimator.ofFloat(binding.btnWelcomeNewHere, View.ALPHA, 1f).setDuration(800)
        val signin = ObjectAnimator.ofFloat(binding.tvWelcomeSignin, View.ALPHA, 1f).setDuration(600)

        AnimatorSet().apply {
            playSequentially(signup, signin)
            startDelay = 200
        }.start()
    }

    private fun setupAction() {
        binding.btnWelcomeNewHere.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, SignUpActivity::class.java))
        }

        binding.tvWelcomeSignin.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, SignInActivity::class.java))
        }
    }

}